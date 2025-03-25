package com.igenesys.utils;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import com.esri.arcgisruntime.security.UserCredential;
import com.igenesys.App;
import com.igenesys.R;

import java.net.MalformedURLException;

public class EsriAuthUtil {


    public static boolean init(Activity activity){
        try{

            Portal portal = new Portal(Constants.getPortalUrl, false);
            portal.setCredential(new UserCredential(Constants.getPortalUserName, Constants.getPortalPassword));
            App.getInstance().setPortal(portal);

            PortalItem portalItem = new PortalItem(portal, Constants.getPortalItemId);
            ArcGISMap map = new ArcGISMap(portalItem);
            com.esri.arcgisruntime.ArcGISRuntimeEnvironment.setApiKey(Constants.ESRI_API_KEY);
            map.loadAsync();
            try {
                App.getInstance().setoAuthConfiguration(new OAuthConfiguration(
                        Constants.getPortalUrl, Constants.clientId,
                        Constants.redirectUri + "://" + Constants.redirectHost
                ));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            DefaultAuthenticationChallengeHandler defaultAuthenticationChallengeHandler = new DefaultAuthenticationChallengeHandler(activity);
            AuthenticationManager.setAuthenticationChallengeHandler(defaultAuthenticationChallengeHandler);
            AuthenticationManager.addOAuthConfiguration(App.getInstance().getoAuthConfiguration());


            return true;
        }catch(Exception ex){
            System.out.println("Exception in EsriAuthUtil.init");
            return false;
        }
    }

}
