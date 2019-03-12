package com.example.asuss410u.appfacebook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ProfilePictureView profilePictureView;
    LoginButton loginButton;
    Button btnLogout, btnOptions;
    TextView txtName, txtEmail, txtFirstName;
    CallbackManager callbackManager;
    String email,Name,first_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Khi gửi một thông điệp lên Sever thì Sever sẽ gửi về một thông điệp thông qua CallbackManager
        // Nhận về app thông qua onActivityResult() bên dưới
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        Anhxa();
        btnOptions.setVisibility(View.INVISIBLE);
        btnLogout.setVisibility(View.INVISIBLE);
        txtFirstName.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);
        // Hàm setReadPermissions để xin các quyền
        // public_profile lấy những thông tin công khai từ người dùng

        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        // CÓ thể nhận thêm quyền với LoginManager và phương thức LogInWithPermissionName
        // nó sẽ mở một giao diện mới nhắc người dùng cấp quyền bổ sung nếu cần
        // VD:
        //LoginManager.getInstance().logInWithReadPermissions(
        //    fragmentOrActivity,
        //    Arrays.asList("email"));

        setLogin_Button();
        setLogOut_Button();
        movetoOptions();

    }

    private void movetoOptions() {
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Options.class);
                startActivity(intent);
            }
        });

    }

    //LogOut
    private void setLogOut_Button() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                btnLogout.setVisibility(View.INVISIBLE);
                txtFirstName.setVisibility(View.INVISIBLE);
                txtEmail.setVisibility(View.INVISIBLE);
                txtName.setVisibility(View.INVISIBLE);
                btnOptions.setVisibility(View.INVISIBLE);
                txtEmail.setText("");
                txtFirstName.setText("");
                txtName.setText("");
                profilePictureView.setProfileId(null);
                loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setLogin_Button() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            //Khi Action FacbookCallback hoạt động thì sẽ trả ra việc xử lý trong hàm onSuccess
            public void onSuccess(LoginResult loginResult) {
                // Trong hàm này, khi người dùng đăng nhập thành công, chúng ta muốn lấy dữ liệu
                // Sử dụng hàm result() bên dưới
                loginButton.setVisibility(View.INVISIBLE);
                btnOptions.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.VISIBLE);
                txtFirstName.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtName.setVisibility(View.VISIBLE);
                result();
            }
            // Thành công thì sẽ trả ra hàm dưới
            @Override
            public void onCancel() {

            }
            // Thất bại cho hàm dưới
            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    // Hàm lấy dữ liệu
    private void result() {

        // Hàm GraphRequest giúp gửi lên server.
        // Truyền 2 tham số AccessToken để chứng thực đăng nhập và đăng xuất
        // Chọn hoạt động của GraphJSONObjectCallback(),
        // Thông điệp trả về sẽ gửi qua đoạn JSONObject
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            // Nhận thông điệp của đoạn JSONObject qua hàm onCompleted() ở dưới
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON",response.getJSONObject().toString());
                try{
                    // Lấy thông tin người dùng và hiển thị lên màn hình
                    // Dùng biến object trên để lấy thông tin từ đoạn JSON nhận về
                    email = object.getString("email");
                    Name  = object.getString("name");
                    first_name = object.getString("first_name");

                    // Có hỗ trợ lấy trực tiếp từ Profile của người dùng hiện tại, lấy Id của người dùng đó
                    // Hoặc có thể lấy từ đoạn JSON như các biến trên
                    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
                    // Sau đó truyền vào các text để hiển thị trên màn hình
                    txtEmail.setText(email);
                    txtFirstName.setText(first_name);
                    txtName.setText(Name);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        // Để có thể lấy về chuỗi JSON thì tạo 1 biến Bundle parameters bên dưới
        Bundle parameters = new Bundle();
        // Biến Bundle sẽ gửi lên server đầu tiên là string "fields"
        // thứ 2 là các giá trị gửi lên cần để lấy thông tin về
        parameters.putString("fields", "name,email,first_name,accounts");
        // Sau đó sẽ gọi lại graphRequest sau đó setParameters cho (Bundle)parameters đã gửi đi ở trên
        graphRequest.setParameters(parameters);
        // và sau cùng là execute
        graphRequest.executeAsync();
    }

    public void Anhxa(){
        profilePictureView = (ProfilePictureView)findViewById(R.id.imageprofilepictureview);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        btnLogout = (Button)findViewById(R.id.btnlogout);
        btnOptions = (Button)findViewById(R.id.btnOptions);
        txtEmail = (TextView)findViewById(R.id.tvEmail);
        txtName = (TextView)findViewById(R.id.tvName);
        txtFirstName = (TextView)findViewById(R.id.tvFirstName);
    }

    // Hàm nhận thông điệp về app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // Để khi chạy app trở về trạng thái sẽ đăng nhập vào thì sử dụng hàm onStart()

    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();
        // Khi đăng nhập thì tự LogOut ra
        super.onStart();
    }
}
