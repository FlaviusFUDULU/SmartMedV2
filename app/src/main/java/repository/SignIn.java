package repository;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ffudulu on 06-Apr-17.
 */
public class SignIn {
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private int status= -2;

    public SignIn(FirebaseAuth mAuth, String email, String password) {
        this.mAuth = mAuth;
        this.email = email;
        this.password = password;
    }

    public int signIntoAccount(){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            status = 0;

        }
        else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                status = -1;
                            }
                            else{
                                status = 2;
                            }
                        }
                    });
        }
        return status;
    }
}
