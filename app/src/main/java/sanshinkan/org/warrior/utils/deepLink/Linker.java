package sanshinkan.org.warrior.utils.deepLink;

import java.util.HashMap;

import sanshinkan.org.warrior.views.activities.Home;
import sanshinkan.org.warrior.views.activities.SplashActivity;

/**
 * Created by apoorvarora on 10/04/17.
 */
public class Linker {
    //Store deep links in the map
    private HashMap<String, Class<?>> map;

    private static volatile Linker sInstance;

    public static final String DEFAULT = "default";
    public static final String HOME_SANSHINKAN_LINK = "sanshinkan://home";
    public static final String SPLASH_LINK = "sanshinkan://splash";

    /**Use default deep links.*/
    private Linker(){
        this.map = Initializor.initialize();
    }

    public static Linker getInstance() {
        if (sInstance == null) {
            synchronized (Linker.class) {
                if (sInstance == null) {
                    sInstance = new Linker();
                }
            }
        }
        return sInstance;
    }


    /**
     * Default deep links mapped with the class names. Use this to manipulate the default deep links.*/
    static class Initializor {
        public static HashMap<String, Class<?>> initialize() {
            HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
            map.put(Linker.HOME_SANSHINKAN_LINK, Home.class);
            map.put(Linker.SPLASH_LINK, SplashActivity.class);
            return map;
        }
    }

    /**
     * Returns the class associated the key.
     *
     * @param key Key to be searched within map to get it's class variable.
     * */
    public Class<?> getClass(String key){
        if(map != null){
            return map.get(key);
        }
        return null;
    }
}
