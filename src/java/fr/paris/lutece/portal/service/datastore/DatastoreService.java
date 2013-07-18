/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.datastore;

import fr.paris.lutece.portal.business.datastore.DataEntity;
import fr.paris.lutece.portal.business.datastore.DataEntityHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.NoDatabaseException;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Datastore Service
 */
public final class DatastoreService
{

    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    private static final Pattern PATTERN_DATASTORE_KEY = Pattern.compile("#dskey\\{(.*?)\\}");
    private static final String VALUE_MISSING = "DS Value Missing";
    private static AbstractCacheableService _cache;
    private static boolean _bDatabase = true;

    /**
     * Private constructor
     */
    private DatastoreService()
    {
    }

    /**
     * Get entity
     *
     * @param strKey The entity's key
     * @param strDefault The default value
     * @return The value
     */
    public static String getDataValue(String strKey, String strDefault)
    {
        try
        {
            if (_bDatabase)
            {
                DataEntity entity = null;

                if (_cache != null)
                {
                    entity = (DataEntity) _cache.getFromCache(strKey);
                }

                if (entity == null)
                {
                    entity = DataEntityHome.findByPrimaryKey(strKey);

                    if (entity == null)
                    {
                        return strDefault;
                    }

                    if (_cache != null)
                    {
                        _cache.putInCache(strKey, entity);
                    }
                }
                return entity.getValue();
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
        return strDefault;
    }

    /**
     * Get entity
     *
     * @param strKey The entity's key
     * @param strValue The value
     */
    public static void setDataValue(String strKey, String strValue)
    {
        try
        {
            if (_bDatabase)
            {
                DataEntity p = new DataEntity(strKey, strValue);
                DataEntity entity = DataEntityHome.findByPrimaryKey(strKey);

                if (entity != null)
                {
                    DataEntityHome.update(p);
                    if (_cache != null)
                    {
                        _cache.removeKey(strKey);
                    }
                }
                else
                {
                    DataEntityHome.create(p);
                }
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
    }

    /**
     * Remove a give key
     *
     * @param strKey The key
     */
    public static void removeData(String strKey)
    {
        try
        {
            if (_bDatabase)
            {
                DataEntityHome.remove(strKey);
                if (_cache != null)
                {
                    _cache.removeKey(strKey);
                }
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
    }

    /**
     * Remove all data where keys begin with a given prefix
     *
     * @param strPrefix The prefix
     */
    public static void removeDataByPrefix(String strPrefix)
    {
        try
        {
            if (_bDatabase)
            {
                List<DataEntity> listEntities = DataEntityHome.findAll();

                for (DataEntity entity : listEntities)
                {
                    if (entity.getKey().startsWith(strPrefix))
                    {
                        removeData(entity.getKey());
                    }
                }
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
    }

    /**
     * Gets a list of key/value where keys are matching a given prefix
     *
     * @param strPrefix The prefix
     * @return The list
     */
    public static ReferenceList getDataByPrefix(String strPrefix)
    {
        ReferenceList list = new ReferenceList();
        try
        {
            if (_bDatabase)
            {
                List<DataEntity> listEntities = DataEntityHome.findAll();

                for (DataEntity entity : listEntities)
                {
                    if (entity.getKey().startsWith(strPrefix))
                    {
                        list.addItem(entity.getKey(), entity.getValue());
                    }
                }
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
        return list;
    }

    /**
     * This method replace keys by their value into a given content
     *
     * @param strSource The string that contains datastore keys
     * @return The string with keys replaced
     */
    public static String replaceKeys(String strSource)
    {
        String result = strSource;

        if (strSource != null)
        {
            Matcher matcher = PATTERN_DATASTORE_KEY.matcher(strSource);

            if (matcher.find())
            {
                StringBuffer sb = new StringBuffer();

                do
                {
                    String strKey = matcher.group(1);
                    String strValue = DatastoreService.getDataValue(strKey, VALUE_MISSING);

                    if (VALUE_MISSING.equals(strValue))
                    {
                        AppLogService.error("Datastore Key missing : " + strKey
                                + " - Please fix to avoid performance issues.");
                    }

                    matcher.appendReplacement(sb, strValue);
                }
                while (matcher.find());

                matcher.appendTail(sb);
                result = sb.toString();
            }
        }

        return result;
    }

    /**
     * Check if a key is available in the datastore
     *
     * @param strKey The key
     * @return True if the key is found otherwise false
     */
    public static boolean existsKey(String strKey)
    {
        try
        {
            if (_bDatabase)
            {
                DataEntity entity = null;
                if (_cache != null)
                {
                    entity = (DataEntity) _cache.getFromCache(strKey);
                }

                if (entity == null)
                {
                    entity = DataEntityHome.findByPrimaryKey(strKey);

                    if (entity == null)
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        catch (NoDatabaseException e)
        {
            disableDatastore(e);
        }
        return false;
    }

    /**
     * Start cache. NB : Cache can't be created at DataStore creation because
     * CacheService uses DatastoreService (Circular reference)
     */
    public static void startCache()
    {
        _cache = new DatastoreCacheService();
        AppLogService.info("Datastore's cache started.");
    }

    private static void disableDatastore(NoDatabaseException e)
    {
        _bDatabase = false;
        AppLogService.error("##### CRITICAL ERROR ##### : Datastore has been disabled due to a NoDatabaseException catched", e);
    }
}
