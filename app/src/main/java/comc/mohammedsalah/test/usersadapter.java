package comc.mohammedsalah.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class usersadapter extends RecyclerView.Adapter <usersadapter.userViewHolder> {

    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
   private Context context;
    List <usersclass> list ;

    public usersadapter (List <usersclass> list){
       this.list = list;
   }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View  v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_list_item,viewGroup,false);
        context =  viewGroup.getContext();
        return new userViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final userViewHolder userViewHolder, int i) {

      String name = list.get(i).getName();
      String phone = list.get(i).getphone();
      String image = list.get(i).getimage();
      userViewHolder.Setusers(name,phone,image);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder{

        TextView name , phone;
        ImageView imageView;
        View view;
        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void Setusers(String text , String ph , String uri){
            name = view.findViewById(R.id.username);
            phone = view.findViewById(R.id.phoneno);
            imageView =view.findViewById(R.id.imageview);
            Glide.with(context).load(uri).into(imageView);
            name.setText(text);
            phone.setText("( "+ph+" )");
        }


    }
}
