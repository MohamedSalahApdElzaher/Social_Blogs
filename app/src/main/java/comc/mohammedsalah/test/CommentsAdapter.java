package comc.mohammedsalah.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter <CommentsAdapter.viewHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    private List <CommrntsClass> list;
    private Context context;

    public CommentsAdapter (List <CommrntsClass> list){
        this.list = list;
    }


    @NonNull
    @Override
    public CommentsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item,viewGroup,false);
        context = viewGroup.getContext();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.viewHolder viewHolder, int i) {

          String commentText = list.get(i).getComments();
          String date = list.get(i).getDate();
          viewHolder.SetComments(commentText,date);



          String user_id = list.get(i).getUserid();
          firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.getResult().exists()){
                       String name = task.getResult().getString("name");
                       String image = task.getResult().getString("image");
                       viewHolder.SetProfilename(name);
                       viewHolder.SetProfileImage(image);
                  }
              }
          });

}

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

           View view;
           TextView username , time , comment  , commentCounter;
           ImageView imageView ;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView ;
        }

        public void SetProfileImage(String uri){
            imageView = view.findViewById(R.id.comment_prof_image);
            Glide.with(context).load(uri).into(imageView);
        }

        public void SetProfilename(String text){
            username = view.findViewById(R.id.comment_username);
            username.setText(text);
        }


        public void SetComments(String text , String comment_time){
            comment = view.findViewById(R.id.comment_text);
            time = view.findViewById(R.id.time_comment);
            time.setText(comment_time);
            comment.setText("\" "+text+" \"");
        }

    }
}
