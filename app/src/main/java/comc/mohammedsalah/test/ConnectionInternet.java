package comc.mohammedsalah.test;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class ConnectionInternet {
    Context context;
    public ConnectionInternet(Context context){
        this.context = context;
    }
    public boolean isConnectToInternt(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()){
                return true;
            }
        }
        return false;
    }
}
