/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
package fr.paris.lutece.portal.service.mail;

import fr.paris.lutece.portal.service.daemon.AppDaemonService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.mail.FileAttachment;
import fr.paris.lutece.util.mail.UrlAttachment;

import java.util.List;


/**
 * Application Mail Service
 */
public final class MailService
{
    private static final String PROPERTY_MAIL_NOREPLY_EMAIL = "mail.noreply.email";
    private static final String BEAN_MAIL_QUEUE = "mailQueue";

    /** Creates a new instance of AppMailService */
    private MailService(  )
    {
    }

    /**
     * Send a message asynchronously. The message is queued until a daemon
     * thread send all awaiting messages
     * @deprecated
     * @param strRecipient The recipient email.
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public static void sendMail( String strRecipient, String strSenderName, String strSenderEmail, String strSubject,
        String strMessage )
    {
        MailItem item = new MailItem(  );
        item.setRecipientsTo( strRecipient );
        item.setSenderName( strSenderName );
        item.setSenderEmail( strSenderEmail );
        item.setSubject( strSubject );
        item.setMessage( strMessage );

        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );
        queue.send( item );
    }

    /**
     * Send a HTML message asynchronously. The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator define in config.properties
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public static void sendMailHtml( String strRecipientsTo, String strSenderName, String strSenderEmail,
        String strSubject, String strMessage )
    {
        sendMailHtml( strRecipientsTo, null, null, strSenderName, strSenderEmail, strSubject, strMessage );
    }

    /**
     * Send a HTML message asynchronously. The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strRecipientsCc The recipients list of the carbon copies .
     * @param strRecipientsBcc The recipients list of the blind carbon copies .
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public static void sendMailHtml( String strRecipientsTo, String strRecipientsCc, String strRecipientsBcc,
        String strSenderName, String strSenderEmail, String strSubject, String strMessage )
    {
        MailItem item = new MailItem(  );
        item.setRecipientsTo( strRecipientsTo );
        item.setRecipientsCc( strRecipientsCc );
        item.setRecipientsBcc( strRecipientsBcc );
        item.setSenderName( strSenderName );
        item.setSenderEmail( strSenderEmail );
        item.setSubject( strSubject );
        item.setMessage( strMessage );
        item.setFormat( MailItem.FORMAT_HTML );

        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );
        queue.send( item );
    }

    /**
     * Send a HTML message asynchronously with the attachements associated to the message . The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     * @param urlsAttachement The List of UrlAttachement Object, containing the URL of attachments associated with their content-location.
     */
    public static void sendMailMultipartHtml( String strRecipientsTo, String strSenderName, String strSenderEmail,
        String strSubject, String strMessage, List<UrlAttachment> urlsAttachement )
    {
        sendMailMultipartHtml( strRecipientsTo, null, null, strSenderName, strSenderEmail, strSubject, strMessage,
            urlsAttachement, null );
    }

    /**
     * Send a HTML message asynchronously with the attachements associated to the message and attached files . The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strRecipientsCc The recipients list of the carbon copies .
     * @param strRecipientsBcc The recipients list of the blind carbon copies .
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     * @param urlsAttachement The List of UrlAttachement Object, containing the URL of attachments associated with their content-location
     * @param filesAttachement The list of attached files.
     */
    public static void sendMailMultipartHtml( String strRecipientsTo, String strRecipientsCc, String strRecipientsBcc,
        String strSenderName, String strSenderEmail, String strSubject, String strMessage,
        List<UrlAttachment> urlsAttachement, List<FileAttachment> filesAttachement )
    {
        MailItem item = new MailItem(  );
        item.setRecipientsTo( strRecipientsTo );
        item.setRecipientsCc( strRecipientsCc );
        item.setRecipientsBcc( strRecipientsBcc );
        item.setSenderName( strSenderName );
        item.setSenderEmail( strSenderEmail );
        item.setSubject( strSubject );
        item.setMessage( strMessage );
        item.setFormat( MailItem.FORMAT_MULTIPART_HTML );
        item.setUrlsAttachement( urlsAttachement );
        item.setFilesAttachement( filesAttachement );

        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );
        queue.send( item );
    }

    /**
     * Send a text message asynchronously. The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public static void sendMailText( String strRecipientsTo, String strSenderName, String strSenderEmail,
        String strSubject, String strMessage )
    {
        sendMailText( strRecipientsTo, null, null, strSenderName, strSenderEmail, strSubject, strMessage );
    }

    /**
     * Send a text message asynchronously. The message is queued until a daemon
     * thread send all awaiting messages
     *@param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strRecipientsCc The recipients list of the carbon copies .
     * @param strRecipientsBcc The recipients list of the blind carbon copies .
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public static void sendMailText( String strRecipientsTo, String strRecipientsCc, String strRecipientsBcc,
        String strSenderName, String strSenderEmail, String strSubject, String strMessage )
    {
        MailItem item = new MailItem(  );
        item.setRecipientsTo( strRecipientsTo );
        item.setRecipientsCc( strRecipientsCc );
        item.setRecipientsBcc( strRecipientsBcc );
        item.setSenderName( strSenderName );
        item.setSenderEmail( strSenderEmail );
        item.setSubject( strSubject );
        item.setMessage( strMessage );
        item.setFormat( MailItem.FORMAT_TEXT );

        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );
        queue.send( item );
    }

    /**
     * Send a text message asynchronously with attached files. The message is queued until a daemon
     * thread send all awaiting messages
     *
     * @param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     * @param filesAttachement The list of attached files.
     */
    public static void sendMailMultipartText( String strRecipientsTo, String strSenderName, String strSenderEmail,
        String strSubject, String strMessage, List<FileAttachment> filesAttachement )
    {
        sendMailMultipartText( strRecipientsTo, null, null, strSenderName, strSenderEmail, strSubject, strMessage,
            filesAttachement );
    }

    /**
     * Send a text message asynchronously with attached files. The message is queued until a daemon
     * thread send all awaiting messages
     *@param strRecipientsTo The list of the main recipients email.Every recipient
     *                   must be separated by the mail separator defined in config.properties
     * @param strRecipientsCc The recipients list of the carbon copies .
     * @param strRecipientsBcc The recipients list of the blind carbon copies .
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     * @param filesAttachement The list of attached files.
     */
    public static void sendMailMultipartText( String strRecipientsTo, String strRecipientsCc, String strRecipientsBcc,
        String strSenderName, String strSenderEmail, String strSubject, String strMessage,
        List<FileAttachment> filesAttachement )
    {
        MailItem item = new MailItem(  );
        item.setRecipientsTo( strRecipientsTo );
        item.setRecipientsCc( strRecipientsCc );
        item.setRecipientsBcc( strRecipientsBcc );
        item.setSenderName( strSenderName );
        item.setSenderEmail( strSenderEmail );
        item.setSubject( strSubject );
        item.setMessage( strMessage );
        item.setFormat( MailItem.FORMAT_MULTIPART_TEXT );
        item.setFilesAttachement( filesAttachement );

        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );
        queue.send( item );
    }

    /**
     * Shutdown the service
     */
    public static void shutdown(  )
    {
        // if there is mails that have not been sent call the daemon to flush the list
        Daemon daemon = AppDaemonService.getDaemon( "mailSender" );

        if ( daemon != null )
        {
            daemon.run(  );
        }
    }

    /**
     * Returns a no reply email address defined in config.properties
     * @return A no reply email
     */
    public static String getNoReplyEmail(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MAIL_NOREPLY_EMAIL );
    }

    /**
     * Returns the mail queue
     * @return the mail queue
     */
    public static IMailQueue getQueue(  )
    {
        IMailQueue queue = (IMailQueue) SpringContextService.getBean( BEAN_MAIL_QUEUE );

        return queue;
    }

    /**
     * Extract a collection of elements to be attached to a mail from an HTML string.
     *
     * The collection contains the Url used for created DataHandler  for each url associated with
     * an HTML tag img, script or link. Those urls must start with the url strBaseUrl.
     *
     * @param strHtml The HTML code.
     * @param strBaseUrl The base url, can be null in order to extract all urls.
     * @param useAbsoluteUrl Determine if we use absolute or relative url for attachement content-location
     * @return a collection of UrlAttachment Object  for created  DataHandler associated with attachment urls.
     */
    public static List<UrlAttachment> getUrlAttachmentList( String strHtml, String strBaseUrl, boolean useAbsoluteUrl )
    {
        return MailUtil.getUrlAttachmentList( strHtml, strBaseUrl, useAbsoluteUrl );
    }
}
