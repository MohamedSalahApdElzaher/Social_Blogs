package comc.mohammedsalah.test;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class postid {
    String postid;
    @Exclude
    public <T extends postid> T withId(@NonNull final String id){
        this.postid = id;
        return (T) this;
    }
}
