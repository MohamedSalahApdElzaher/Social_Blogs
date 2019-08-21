package comc.mohammedsalah.test;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.viewHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    private Context context;
      List<Blogpost> blogpostList;
    private View v;

    public BlogRecyclerAdapter(List<Blogpost> blogpostList){
          this.blogpostList = blogpostList;
      }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_item,viewGroup,false);
          context = viewGroup.getContext();
          firebaseAuth = FirebaseAuth.getInstance();

          return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {

          String textdesc = blogpostList.get(i).getPost_description();
          viewHolder.SetDescription(textdesc);

         String textTitle = blogpostList.get(i).getPost_title();
         viewHolder.SetTitle(textTitle);

         String date = blogpostList.get(i).getDate();
         viewHolder.SetDate(date);

         String uri = blogpostList.get(i).getImage_uri();
         viewHolder.SetBlogImage(uri);

        final String postId = blogpostList.get(i).postid;


         final String user_id = blogpostList.get(i).getId();
        final String current_id=firebaseAuth.getCurrentUser().getUid();

        if (current_id.equals(user_id)){ // that mean the post is belong to who logged in
            viewHolder.deletepost.setVisibility(View.VISIBLE);
        }

        progressDialog = new ProgressDialog(context);

        viewHolder.deletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Are You Sure to delete this post ?\nهل ترغب في ازاله هذا البوست ؟");
                alert.setTitle("Delete\n ازاله");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Deleting post...\nجارى المسح");
                        progressDialog.create();
                        progressDialog.show();
                         if (firebaseAuth.getCurrentUser() != null) {
                             firebaseFirestore.collection("posts").document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                   try{ // Handel an Exception Error
                                       blogpostList.remove(i);
                                       progressDialog.cancel();
                                       Intent intent = new Intent(context,home.class); // as refresh
                                       context.startActivity(intent);
                                       Toast.makeText(context,"deleted Successful!\nتم المسح بنجاح",Toast.LENGTH_SHORT).show();
                                   }catch (Exception e){
                                       Toast.makeText(context,"Error :" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                   }

                                 }
                             });
                         }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.setCancelable(true);
                    }
                });

                alert.create();
                alert.show();

            }
        });

         firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.getResult().exists()){
                       String name = task.getResult().getString("name");
                       String uri = task.getResult().getString("image");
                       String phone = task.getResult().getString("phone");
                       viewHolder.SetUsername(name , phone);
                       viewHolder.SetProfileImage(uri);
                   }
             }
         });

         // tab a like
        firebaseFirestore.collection("posts").document(postId).collection("likes").document(current_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){
                             try {
                                    // can't resolve the textColor !!
                                     viewHolder.likeimaeg.setImageDrawable(context.getDrawable(R.drawable.ic_liked));
                                     viewHolder.textLikecounter.setTextColor(ContextCompat.getColor(context, R.color.love));
                                    viewHolder.sadimage.setEnabled(false);
                                    viewHolder.shameimage.setEnabled(false);
                                    viewHolder.handimage.setEnabled(false);
                                    viewHolder.hahaimage.setEnabled(false); // unEnable another emoje
                                }catch (Exception ex){
                                    Log.d("Error :" , ex.getMessage());
                                }


                        }else {

                            try {
                                viewHolder.textLikecounter.setTextColor(ContextCompat.getColor(context, R.color.shame));
                                viewHolder.likeimaeg.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_24dp));
                                viewHolder.sadimage.setEnabled(true);
                                viewHolder.shameimage.setEnabled(true);
                                viewHolder.handimage.setEnabled(true);
                                viewHolder.hahaimage.setEnabled(true); // unEnable another emoje
                            }catch (Exception ex){
                                Log.d("Error :" , ex.getMessage());
                            }
                        }
                    }
                });


        // tab a hand
        firebaseFirestore.collection("posts").document(postId).collection("hand").document(current_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){
                                viewHolder.handimage.setImageDrawable(context.getDrawable(R.drawable.handed));
                             //   viewHolder.handcounter.setTextColor(ContextCompat.getColor(context, R.color.hand));
                                viewHolder.sadimage.setEnabled(false);
                                viewHolder.likeimaeg.setEnabled(false);
                                viewHolder.shameimage.setEnabled(false);
                                viewHolder.hahaimage.setEnabled(false); // unEnable another emoje

                        }else {
                            viewHolder.handimage.setImageDrawable(context.getDrawable(R.drawable.tadamn));
                            viewHolder.sadimage.setEnabled(true);
                            viewHolder.shameimage.setEnabled(true);
                            viewHolder.likeimaeg.setEnabled(true);
                            viewHolder.hahaimage.setEnabled(true); // unEnable another emoje
                           // viewHolder.handcounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });

        // tab a Haha
        firebaseFirestore.collection("posts").document(postId).collection("haha").document(current_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){

                                viewHolder.hahaimage.setImageDrawable(context.getDrawable(R.drawable.hahaed));
                             //   viewHolder.hahacounter.setTextColor(ContextCompat.getColor(context, R.color.haha));
                                viewHolder.sadimage.setEnabled(false);
                                viewHolder.shameimage.setEnabled(false);
                                viewHolder.handimage.setEnabled(false);
                                viewHolder.likeimaeg.setEnabled(false); // unEnable another emoje


                        }else {
                            viewHolder.hahaimage.setImageDrawable(context.getDrawable(R.drawable.hahah));
                            viewHolder.sadimage.setEnabled(true);
                            viewHolder.handimage.setEnabled(true);
                            viewHolder.shameimage.setEnabled(true);
                            viewHolder.likeimaeg.setEnabled(true); // unEnable another emoje
                           // viewHolder.hahacounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });


        // tab a Sad
        firebaseFirestore.collection("posts").document(postId).collection("sad").document(current_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){

                                viewHolder.sadimage.setImageDrawable(context.getDrawable(R.drawable.sadded));
                              //  viewHolder.sadcounter.setTextColor(ContextCompat.getColor(context, R.color.sad));
                                viewHolder.likeimaeg.setEnabled(false); // unEnable another emoje
                                viewHolder.hahaimage.setEnabled(false);
                                viewHolder.shameimage.setEnabled(false);

                        }else {
                            viewHolder.sadimage.setImageDrawable(context.getDrawable(R.drawable.ic_sad));
                            viewHolder.hahaimage.setEnabled(true);
                            viewHolder.shameimage.setEnabled(true);
                            viewHolder.likeimaeg.setEnabled(true); // unEnable another emoje
                         //   viewHolder.sadcounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });

        // tab a shame
        firebaseFirestore.collection("posts").document(postId).collection("shame").document(current_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){

                                viewHolder.shameimage.setImageDrawable(context.getDrawable(R.drawable.shamed));
                              //  viewHolder.shamecounter.setTextColor(ContextCompat.getColor(context, R.color.shamed));
                                viewHolder.likeimaeg.setEnabled(false); // unEnable another emoje
                                viewHolder.hahaimage.setEnabled(false);
                                viewHolder.sadimage.setEnabled(false);
                                viewHolder.handimage.setEnabled(false);


                        }else {
                            viewHolder.shameimage.setImageDrawable(context.getDrawable(R.drawable.ic_ashame));
                            viewHolder.hahaimage.setEnabled(true);
                            viewHolder.likeimaeg.setEnabled(true);
                            viewHolder.handimage.setEnabled(true);
                         //   viewHolder.shamecounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                            viewHolder.sadimage.setEnabled(true);   // unEnable another emoje

                        }
                    }
                });

        // Likes
         viewHolder.likeimaeg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View vi) {
                viewHolder.textLikecounter.setTextColor(ContextCompat.getColor(context,R.color.love));
                 firebaseFirestore.collection("posts").document(postId).collection("likes").document(current_id)
                         .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if (!task.getResult().exists()){
                             Snackbar.make(v, "خد قلبي ناو",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                             Map <String , Object > map = new HashMap<>();
                             map.put("like'sTime", FieldValue.serverTimestamp()); //time when user click like image
                             firebaseFirestore.collection("posts").document(postId).collection("likes").document(current_id)
                                     .set(map);
                         }else {
                             firebaseFirestore.collection("posts").document(postId).collection("likes").document(current_id)
                                     .delete();
                             viewHolder.textLikecounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                         }
                     }
                 });

             }
         });
        firebaseFirestore.collection("posts").document(postId).collection("likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                      int con = queryDocumentSnapshots.size();
                       viewHolder.SetLikeCounter(con);
                }else {
                    viewHolder.SetLikeCounter(0);
                }
            }
        });

        // haha
        viewHolder.hahaimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                viewHolder.hahacounter.setTextColor(ContextCompat.getColor(context,R.color.haha));
                firebaseFirestore.collection("posts").document(postId).collection("haha").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Snackbar.make(v, "يخربيت الضحك",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                            Map <String , Object > map = new HashMap<>();
                            firebaseFirestore.collection("posts").document(postId).collection("haha").document(current_id)
                                    .set(map);
                        }else {
                            firebaseFirestore.collection("posts").document(postId).collection("haha").document(current_id)
                                    .delete();
                            viewHolder.hahacounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });
            }
        });

        firebaseFirestore.collection("posts").document(postId).collection("haha").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int con = queryDocumentSnapshots.size();
                    viewHolder.SethahaCounter(con);
                }else {
                    viewHolder.SethahaCounter(0);
                }
            }
        });

        // Sad emoje
        viewHolder.sadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                viewHolder.sadcounter.setTextColor(ContextCompat.getColor(context,R.color.sad));
                firebaseFirestore.collection("posts").document(postId).collection("sad").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Snackbar.make(v, "ساد ورب العباد",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                            Map <String , Object > map = new HashMap<>();
                            // map.put("haha'sTime", FieldValue.serverTimestamp()); //time when user click like image
                            firebaseFirestore.collection("posts").document(postId).collection("sad").document(current_id)
                                    .set(map);
                        }else {
                            firebaseFirestore.collection("posts").document(postId).collection("sad").document(current_id)
                                    .delete();
                            viewHolder.sadcounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });
            }
        });
        firebaseFirestore.collection("posts").document(postId).collection("sad").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int con = queryDocumentSnapshots.size();
                    viewHolder.SetsadCounter(con);
                }else {
                    viewHolder.SetsadCounter(0);
                }
            }
        });


        // shame emoje
        viewHolder.shameimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                   viewHolder.shamecounter.setTextColor(ContextCompat.getColor(context,R.color.shamed));
                firebaseFirestore.collection("posts").document(postId).collection("shame").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Snackbar.make(v, "يالهوى ع الاحراج",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                            Map <String , Object > map = new HashMap<>();
                            firebaseFirestore.collection("posts").document(postId).collection("shame").document(current_id)
                                    .set(map);
                        }else {
                            firebaseFirestore.collection("posts").document(postId).collection("shame").document(current_id)
                                    .delete();
                            viewHolder.shamecounter.setTextColor(ContextCompat.getColor(context,R.color.shame));

                        }
                    }
                });
            }
        });
        firebaseFirestore.collection("posts").document(postId).collection("shame").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int con = queryDocumentSnapshots.size();
                    viewHolder.SetshameCounter(con);
                }else {
                    viewHolder.SetshameCounter(0);
                }
            }
        });


        // Hand emoje
        viewHolder.handimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                  viewHolder.handcounter.setTextColor(ContextCompat.getColor(context,R.color.hand));
                firebaseFirestore.collection("posts").document(postId).collection("hand").document(current_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Snackbar.make(v, "احترمه فشخ",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                            Map <String , Object > map = new HashMap<>();
                            firebaseFirestore.collection("posts").document(postId).collection("hand").document(current_id)
                                    .set(map);
                        }else {
                            firebaseFirestore.collection("posts").document(postId).collection("hand").document(current_id)
                                    .delete();
                            viewHolder.handcounter.setTextColor(ContextCompat.getColor(context,R.color.shame));
                        }
                    }
                });
            }
        });
        firebaseFirestore.collection("posts").document(postId).collection("hand").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int con = queryDocumentSnapshots.size();
                    viewHolder.SethandCounter(con);
                }else {
                    viewHolder.SethandCounter(0);
                }
            }
        });

        // Comments
           viewHolder.PostComments.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View vi) {
                   Intent intent = new Intent(context , Comment.class);
                   intent.putExtra("postId",postId);
                    context.startActivity(intent);
               }
           });

    }

    @Override
    public int getItemCount() {
        return blogpostList.size();
    }

    public class viewHolder extends  RecyclerView.ViewHolder{

           View view;
           TextView mTextDesc , mTextTitle , hahacounter , sadcounter,shamecounter , handcounter;
           TextView mTextdate;
           ImageView imageView , likeimaeg , shameimage , hahaimage , handimage , sadimage , PostComments , deletepost;
           TextView username , phone , textLikecounter;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            likeimaeg = view.findViewById(R.id.like_post);
            PostComments = view.findViewById(R.id.comment_post);
            deletepost = view.findViewById(R.id.delete_post);
            shameimage = view.findViewById(R.id.shame_post);
            handimage = view.findViewById(R.id.hand_post);
            sadimage = view.findViewById(R.id.sad_post);
            hahaimage = view.findViewById(R.id.haha_post);

        }

        public void SetDescription(String text){
               mTextDesc = view.findViewById(R.id.text_post_description);
               mTextDesc.setText(text);
        }
        public void SetTitle(String text){
            mTextTitle = view.findViewById(R.id.text_post_Title);
            mTextTitle.setText(text);
        }
        public void SetDate(String date){
            mTextdate = view.findViewById(R.id.text_postdate);
            mTextdate.setText(date);
        }
        public void SetBlogImage(String uri){
            imageView = view.findViewById(R.id.image_post);
            Glide.with(context).load(uri).into(imageView);
        }

        public void SetUsername(String name , String numb){
            username = view.findViewById(R.id.text_username);
            phone = view.findViewById(R.id.post_text_phone);
            username.setText(name);
            phone.setText("( "+numb+" )");
        }
        public void SetProfileImage(String uri){
            imageView = view.findViewById(R.id.post_prof_image);
            Glide.with(context).load(uri).into(imageView);
        }
        public void SetLikeCounter(int count){
            textLikecounter = view.findViewById(R.id.like_counter);
            textLikecounter.setText(count + " احببته");
        }

        public void SethahaCounter(int i) {
            hahacounter = view.findViewById(R.id.haha_counter);
            hahacounter.setText(i + " هاها");
        }

        public void SetsadCounter(int i) {
            sadcounter = view.findViewById(R.id.Sad_counter);
            sadcounter.setText(i + " احزنني");
        }

        public void SetshameCounter(int i) {
            shamecounter = view.findViewById(R.id.ashame_counter);
            shamecounter.setText(i + " احرجني");
        }
        public void SethandCounter(int i) {
            handcounter = view.findViewById(R.id.Hand_counter);
            handcounter.setText(i + " احترمه");
        }
    }
}
