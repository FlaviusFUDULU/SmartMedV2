//package repository;
//
//import android.app.Activity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import users.UserMedic;
//
///**
// * Created by ffudulu on 16-May-17.
// */
//
//public class GetUserMedicInformation extends Activity{
//
//    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
//    private UserMedic userMedic = null;
//
//    public GetUserMedicInformation() {
//        while (userMedic != null) {
//            databaseRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        for (DataSnapshot dss : ds.getChildren()) {
//                            if (dss.child(firebaseUser.getUid()).getValue() != null) {
//                                userMedic = new UserMedic(
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getEmail(),
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getFirstName(),
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getHospitalName(),
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getLastName(),
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getRank(),
//                                        dss.child(firebaseUser.getUid())
//                                                .getValue(UserMedic.class).getSectionName()
//                                );
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
//
//
//    public UserMedic getUserMedicDataFromDB() {
//        return userMedic;
//    }
//
//}
//
////    @Override
////    public void run() {
////        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
////
////}
