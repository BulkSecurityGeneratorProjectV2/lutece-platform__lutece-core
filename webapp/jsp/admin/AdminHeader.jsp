<%@ page import="fr.paris.lutece.portal.service.util.AppPathService" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page buffer="1024kb" %>
<%@ page autoFlush="false" %>

<jsp:useBean id="adminMenu" scope="session" class="fr.paris.lutece.portal.web.admin.AdminMenuJspBean" />
<!DOCTYPE html >
<html lang="fr" xml:lang="fr">
<head>
<title>LUTECE - Administration</title>
<base href="<%= AppPathService.getBaseUrl( request ) %>"></base>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<link rel="stylesheet" type="text/css" href="css/portal_admin.css" title="lutece_admin" />
<!--[if IE]>
<style>
    #menu-main li ul { display: none; }
    #menu-main li:hover ul, #menu-main li.over ul {display: block; }
    #menu-main li ul li { height: 30px; line-height: 30px; }
</style>
<![endif]-->
<!--[if IE 6]>
    <link rel="stylesheet" type="text/css" href="css/ie/portal_admin_ie6.css" title="lutece_admin_ie8" />
<![endif]-->
<!--[if IE 7]>
    <link rel="stylesheet" type="text/css" href="css/ie/portal_admin_ie7.css" title="lutece_admin_ie8" />
<![endif]-->
<!--[if IE]>
    <link rel="stylesheet" type="text/css" href="css/ie/portal_admin_ie8.css" title="lutece_admin_ie8" />
<![endif]-->
<link rel="stylesheet" type="text/css" href="css/print_admin.css" media="print" />
<link rel="stylesheet" type="text/css" href="js/jquery/plugins/autocomplete/jquery.autocomplete.css" />
<link rel="stylesheet" type="text/css" href="/js/jscalendar/calendar-lutece.css" />
<link rel="stylesheet" type="text/css" href="js/jquery/plugins/treeview/jquery.treeview.css"/>
<link rel="stylesheet" type="text/css" href="js/jquery/plugins/ui/datepicker/ui.datepicker.css"/>
<link rel="stylesheet" type="text/css" href="js/jquery/plugins/jpassword/jpassword.css"/>
<script type="text/javascript" src="js/tools.js"></script>
<script src="js/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="js/jquery/plugins/treeview/jquery.cookie.js" type="text/javascript"></script>
<script src="js/jquery/plugins/treeview/jquery.treeview.pack.js" type="text/javascript"></script>
<script src="js/jquery/plugins/ui/datepicker/ui.datepicker.js" type="text/javascript"></script>
<script src="js/jquery/plugins/ui/datepicker/ui.datepicker-fr.js" type="text/javascript"></script>
<script src="js/jquery/plugins/jpassword/jquery.jpassword.pack.js" type="text/javascript"></script>
<script src="js/jquery/plugins/autocomplete/jquery.autocomplete.pack.js" type="text/javascript"></script>
<script src="js/jquery/plugins/generatepassword/jquery.password.min.js" type="text/javascript"></script>
<!--[if IE 6]>
<!-- Hack for menu -->
    <script type="text/javascript">
        startList = function() {
            if (document.all && document.getElementById) {
                var navRoot = document.getElementById("menu-main");
                for (i=0; i<navRoot.childNodes.length; i++) {
                    var node = navRoot.childNodes[i];
                    if (node.nodeName=="LI") {
                        node.onmouseover=function() {
                            this.className+=" over";
                        }
                        node.onmouseout=function() {
                            this.className=this.className.replace(" over", "");
                        }
                    }
                }
            }
        }
        window.onload=startList;
   </script>
<![endif]-->
</head>
<body>
<%-- Display the admin menu --%>
<%= adminMenu.getAdminMenuHeader( request ) %>