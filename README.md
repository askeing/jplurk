# jPlurk

Java Plurk API that use [PLURK API 1.0](http://www.plurk.com/API/1.0/).

This project was branched from [jPlurk - Plurk Official API Binding in Java](http://code.google.com/p/jplurk/) (Google Code) rev 520.

There is another project **jplurk-oauth** for [PLURK API 2.0](http://www.plurk.com/API/).


## Introduction

jPlurk v2 is Java Plurk API that use [PLURK API 1.0](http://www.plurk.com/API/1.0/).

All implementation can be found in the PlurkActionSheet class. We use this class to map Plurk Official API and java source code.


### The evolution of jPlurk

The jPlurk v1 and other unofficial plurk api implement plurk function by parsing html and pretend the web browser (submiting forms, keeping cookies, etc). Until Plurk Official API is released (5, Dec, 2009), we decide to renew our project to following the stardard. Stop the jPlurk v1 and propose the new architecture of jPlurk.

jPlurk v2 use Plurk Official API, all implementation can be found in the PlurkActionSheet class. We use this class to map Plurk Official API and java source code. It keep the metadata for uri, required fields, request method, connection type. We use @Meta and @Validation to describe the functions. Here is the sample of getFriendsByOffset method:

    @Meta(uri = "/FriendsFans/getFriendsByOffset", require = { "api_key", "user_id" })
    @Validation({ @Validator(field = "offset", validator = NonNegativeIntegerValidator.class) })
    public HttpUriRequest getFriendsByOffset(Map<String, String> params)
                                    throws PlurkException {
                    return prepare("getFriendsByOffset", params);
    }


### The convention of jPlurk v2 design

* The nameing are almost mapping to Plurk Official API.
* PlurkClient will return either JSONObject or JSONArrayObject.
* It will return null, if any excpetions raising or http status is not 200 OK.


## Quick Tutorial

### Create Config File

You must prepare the jplurk.properties file, the path is:

    ~/jplurk.properties

If you using Windows, file path is User Folder, ex:

    C:\Documents and Settings\%UserName%\jplurk.properties

or

    C:\Users\%UserName%\jplurk.properties

It is a simple Property File, you need to setup api_key attribute at least. You also can setup language if you want:

    api_key=[your_api_key]
    lang=tr_ch

And you can set proxy if you want：

    default_proxy_host = proxy.mydomain.com
    default_proxy_port = 3128
    #default_proxy_user = username
    #default_proxy_password = yourpwd


### Using jPlurk

    import javax.swing.JOptionPane;
    
    import com.google.jplurk.PlurkClient;
    import com.google.jplurk.PlurkSettings;
    import com.google.jplurk.Qualifier;
    import com.google.jplurk.exception.PlurkException;
    
    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;
    
    public class PlurkIt {
        public static void main(String[] args) throws PlurkException {
            PlurkSettings settings = new PlurkSettings();
            PlurkClient client = new PlurkClient(settings);
            JSONObject ret = client.login(
                    JOptionPane.showInputDialog("id"),
                    JOptionPane.showInputDialog("password"));
    
            ret = client.plurkAdd(
                "Plurk It with jPlurk http://code.google.com/p/jplurk/wiki/jplurkV2",
                Qualifier.SHARES);
        }
    }

Whan you using jPlurk, you need creat PlurkSettings to initiate PlurkClient. PlurkSettings will load api_key by reading jplurk.properties file, and api_key will be used in the interaction between PlurkClient and Plurk API . Almost every Plurk API services require login before, so jPlurk do not checking which service do not need login. Please login before you using Plurk API.

    JSONObject ret = client.login(
        JOptionPane.showInputDialog("id"),
        JOptionPane.showInputDialog("password"));

If login success, you can using jPlurk method, just like plurkAdd :

    client.plurkAdd("Plurk It with jPlurk http://code.google.com/p/jplurk/w/edit/jplurkV2", Qualifier.SHARES);

or getUnreadPlurks :

    client.getUnreadPlurks();


### How to Setup Proxy

There are three ways to setup proxy in jPlurk.


#### Set Proxy in Config File

Setup proxy in jplurk.properties file.


#### Using Dynamic Property

jPlurk is Java Library. You can set it using Dynamic Property proxyHost :

    java -DproxyHost=proxy.mydomain.com your.plurk.agent.Main 

And you can using proxyPort :

    java -DproxyHost=proxy.mydomain.com -DproxyPort=3128 your.plurk.agent.Main 

If you need authorisation, using proxyUser and proxyPassword :

    java -DproxyHost=proxy.mydomain.com -DproxyPort=3128 -DproxyUser=username -DproxyPassword=yourpwd your.plurk.agent.Main 


#### Set Proxy in Program

Using ProxyProvider in program.

    import javax.swing.JOptionPane;
    
    import org.json.JSONObject;
    
    import com.google.jplurk.PlurkClient;
    import com.google.jplurk.PlurkSettings;
    import com.google.jplurk.Qualifier;
    import com.google.jplurk.exception.PlurkException;
    import com.google.jplurk.net.ProxyProvider;
    
    public class PlurkIt {
        public static void main(String[] args) throws PlurkException {
            ProxyProvider.setProvider("proxy.mydomain.com", 3128);
            PlurkSettings settings = new PlurkSettings();
            PlurkClient client = new PlurkClient(settings);
            JSONObject ret = client.login(
                    JOptionPane.showInputDialog("id"),
                    JOptionPane.showInputDialog("password"));
            System.out.println(ret);
    
            ret = client.plurkAdd("Plurk It with jPlurk http://code.google.com/p/jplurk/wiki/jplurkV2", Qualifier.SHARES);
            System.out.println(ret);
        }
    }


## More Information

* [Introduction](http://code.google.com/p/jplurk/wiki/Introduction)
* [jPlurk v2 Quick Tutorial and Implementation State](http://code.google.com/p/jplurk/wiki/jplurkV2)
* [Setting Proxy for jPlurk](http://code.google.com/p/jplurk/wiki/Using_Proxy_V2)
* [How to set up the environment for developing](http://code.google.com/p/jplurk/wiki/DeveloperGuide), we suggest to use Maven 2.
* [User Guide for jPlurk v2](http://code.google.com/p/jplurk/wiki/UserGuide)
* [Example](http://code.google.com/p/jplurk/wiki/Example_Code_Using_jPlurk_V2)
* [FAQ](http://code.google.com/p/jplurk/wiki/FAQ)
