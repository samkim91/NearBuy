package com.example.trading.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trading.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    // 로그에 사용할 태그
    String TAG = "PhoneAuthActivity";

    String mVerificationID;

    // 파이어베이스 로그인 객체
    FirebaseAuth mAuth;

    // 메시지를 다시 보내게 하는 토큰
    PhoneAuthProvider.ForceResendingToken mResendToken;
    // todo.. 인증상태를 변경해주는 콜백??
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    EditText mPhoneNum;
    EditText mVerificationNum;

    Button verifyBtn;
    Button signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //xml 파일에서 배치한 개체들을 이어줌
        mPhoneNum = findViewById(R.id.phoneNum);
        mVerificationNum = findViewById(R.id.verificationNum);

        verifyBtn = findViewById(R.id.verifyBtn);
        signinBtn = findViewById(R.id.signinBtn);
        signinBtn.setEnabled(false);

        // 인증 초기화함
        mAuth = FirebaseAuth.getInstance();

        // 메시지를 보낼 때 문구를 한글로 설정
        mAuth.setLanguageCode("kr");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: "+ phoneAuthCredential);
                // 인증요청을 보내고 인증번호를 받았을 때, 자동으로 인증절차가 실행되게끔 함.
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // 인증이 실패했을 때 실행되는 메소드. 휴대폰 번호가 유효하지 않을 때와 같은 경우에 발생함.
                Log.w(TAG, "onVerificationFailed: "+ e);

                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    // 인증이 유효하지 않을 경우 알려준다.
                    Log.d(TAG, "FirebaseAuthInvalidCredentialsException");
                }else if(e instanceof FirebaseTooManyRequestsException){
                    // 요청이 초과했을 때 알려준다. 무료 사용이기에 번호당 제한이 있음... 실제 번호로 5번 연속으로 하니까 막힘... 몇 시간 텀인지는 모르겠음.
                    Log.d(TAG, "FirebaseTooManyRequestsException");
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // 입력된 번호로 문자 인증 코드가 보내졌을 때, 실행되는 메소드.
                Log.d(TAG, "onCodeSent: " + s);

                // 나를 식별하는 아이디 s와 재전송토큰을 지정하고 나중에 쓸 수 있게 한다.
                mVerificationID = s;
                mResendToken = forceResendingToken;

                AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                builder.setMessage("인증코드가 발송되었습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        };

        verifyBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 인증번호 보내기 버튼을 누르면 코드가 입력된 번호로 전송되도록 함. 메소드
                String phoneNum = mPhoneNum.getText().toString();
                if(!phoneNum.equals("")){
                    startPhoneNumVerification(phoneNum);
                    signinBtn.setEnabled(true);
                }else{
                    Toast.makeText(VerificationActivity.this, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        signinBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인 버튼을 누르면 입력한 코드가 맞는지 검사하도록 함. 메소드
                String code = mVerificationNum.getText().toString();
                if(!code.equals("")){
                    verifyPhoneNumWithCode(mVerificationID, code);
                }else{
                    Toast.makeText(VerificationActivity.this, "인증코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 전화번호 인증을 시작하는 메소드
    public void startPhoneNumVerification(String phoneNum){
        // 휴대폰에 메시지를 보낸다.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,   // 메시지 받을 폰 번호
                60,         // 제한시간
                TimeUnit.SECONDS,       // 시간단위를 초로 하겠다는 것
                this,   // 콜백과 연결된 액티비티
                mCallbacks      // todo.. OnVerificationStateChangedCallbacks..
        );
    }

    public void verifyPhoneNumWithCode(String verificationID, String code){
        Log.d(TAG, "verifyPhoneNumWithCode");
        // onCodeSent에서 받은 나를 식별할 수 있는 verificationID를 인증코드와 합쳐서 인증서를 만든다.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);

        //이 인증서를 검증 결과에 따라서 통과시킬지, 안 시킬지 정하는 메소드
        signInWithPhoneAuthCredential(credential);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        // 코드의 검증결과에 따라 통과할지 안 할지 정함.
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // 검사결과 성공
                    Log.d(TAG, "signInWithCredential : Success");
//                    FirebaseUser user = task.getResult().getUser();
                    Toast.makeText(VerificationActivity.this, "인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    intent.putExtra("phoneNum", mPhoneNum.getText().toString());

                    startActivity(intent);
                }else{
                    // 검사결과 실패
                    Log.w(TAG, "signInWithCredential : Failure", task.getException());
                    Toast.makeText(VerificationActivity.this, "인증에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        // 유효하지 않은 코드인지 본다.
                        Log.d(TAG, "Invalid code");
                    }
                }
            }
        });
    }
}
