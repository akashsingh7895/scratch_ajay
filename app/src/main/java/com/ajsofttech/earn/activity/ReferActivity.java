package com.ajsofttech.earn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.fragments.ContactFragment;
import com.ajsofttech.earn.fragments.ForgotFragment;
import com.ajsofttech.earn.fragments.GoldFragment;
import com.ajsofttech.earn.fragments.LeaderBoardFragment;
import com.ajsofttech.earn.fragments.PlatinumFragment;
import com.ajsofttech.earn.fragments.ProfileFragment;
import com.ajsofttech.earn.fragments.ReferFragment;
import com.ajsofttech.earn.fragments.SilverFragment;
import com.ajsofttech.earn.fragments.WalletFragment;
import com.ajsofttech.earn.utils.Constant;

public class ReferActivity extends AppCompatActivity {

    private String type;
    private ReferActivity activity;
    private Fragment fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        activity = this;
        type = getIntent().getStringExtra("type");

        if (type != null) {
            switch (type) {
                case "changePassword":
                    fm = ForgotFragment.newInstance();
                    break;

                case "wallet":
                    fm = WalletFragment.newInstance();
                    break;
                case "contact":
                    fm = ContactFragment.newInstance();
                    break;
                case "Profile":
                    fm = ProfileFragment.newInstance();
                    break;
                case "refer":
                    fm = ReferFragment.newInstance();
                    break;
                case "Silver Scratch":
                    fm = SilverFragment.newInstance();
                    break;
                case "Platinum Scratch":
                    fm = PlatinumFragment.newInstance();
                    break;
                case "Gold Scratch":
                    fm = GoldFragment.newInstance();
                    break;
                case "LeaderBoard":
                    fm = LeaderBoardFragment.newInstance();
                    break;

            }
            if (fm != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layout_refer, fm).commit();
            }
        } else {
            Constant.showToastMessage(activity, "Something Went Wrong...");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}