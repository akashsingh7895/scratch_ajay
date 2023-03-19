package com.ajsofttech.earn.fragments;



import static com.ajsofttech.earn.FirebaseProperties.homelink;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ajsofttech.earn.activity.SpinWheelActivity;

import com.ajsofttech.earn.activity.SplashActivity;
import com.ajsofttech.earn.ads.AdsConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.activity.MainActivity;
import com.ajsofttech.earn.activity.ReferActivity;
import com.ajsofttech.earn.utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {


    private CardView referCardView, walletCardView, dailyCheckIn, silverCardView, platinumCardView, goldCardView, QurekaCardView, Qureka;
    private Context activity;
    private LinearLayout ads_click_one;
    private TextView ads_click_two;
    private String url = "https://171.go.qureka.com/intro";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String mainurl;
    String mainurl2;
    long mainurl3;



    public HomeFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = context;
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        referCardView = view.findViewById(R.id.refer_cardView);
        walletCardView = view.findViewById(R.id.walletCardView);
        dailyCheckIn = view.findViewById(R.id.daily_check_in);
        silverCardView = view.findViewById(R.id.silverCardView);
        platinumCardView = view.findViewById(R.id.platinumCardView);
        goldCardView = view.findViewById(R.id.goldCardView);
        QurekaCardView = view.findViewById(R.id.QurekaCardView);
        Qureka = view.findViewById(R.id.Qureka);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String[] home = new String[1];
        final String[] homeb = new String[1];
        final long[] homeu = new long[1];

        firebaseDatabase = FirebaseDatabase.getInstance();



        DatabaseReference myRef1 = database.getReference("homebottom");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                homeb[0] = dataSnapshot.getValue(String.class);
//                mainurl2 = homeb[0];
              //  Toast.makeText(activity, "test2 : "+homeb[0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        onClick();
        return view;
    }

    private void onClick() {
        Qureka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
                customIntent.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                openCustomTab(getActivity(), customIntent.build(), Uri.parse(homelink));
            }
        });



        referCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsConfig.showAd(getActivity());
                try {
                    Intent policyintent = new Intent(activity, ReferActivity.class);
                    policyintent.putExtra("type", "refer");
                    startActivity(policyintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }


            }
        });

        walletCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AdsConfig.showAd(getActivity());
                try {
                    Intent policyintent = new Intent(activity, ReferActivity.class);
                    policyintent.putExtra("type", "wallet");
                    startActivity(policyintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }

            }
        });

        platinumCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent policyintent = new Intent(activity, ReferActivity.class);
                    policyintent.putExtra("type", "Platinum Scratch");
                    startActivity(policyintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }

            }
        });


        dailyCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsConfig.showAd(getActivity());
                checkDaily();
            }
        });

        silverCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent policyintent = new Intent(activity, SpinWheelActivity.class);
                    policyintent.putExtra("type", "Silver Scratch");
                    startActivity(policyintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }
            }
        });

        goldCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent policyintent = new Intent(activity, ReferActivity.class);
                    policyintent.putExtra("type", "Gold Scratch");
                    startActivity(policyintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }
            }
        });




        QurekaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent qurekaintent = new Intent("android.intent.action.VIEW", Uri.parse("http://334.game.qureka.com"));
                    startActivity(qurekaintent);
                } catch (Exception e) {
                    Constant.showToastMessage(activity, e.getMessage());
                }

            }
        });

    }


    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = "com.android.chrome";
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        } else {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private void checkDaily() {
        if (Constant.isNetworkAvailable(activity)) {
            String currentDate = Constant.getString(activity, Constant.TODAY_DATE);
            Log.e("TAG", "onClick: Current Date" + currentDate);
            String last_date = Constant.getString(activity, Constant.LAST_DATE);
            Log.e("TAG", "onClick: last_date Date" + last_date);
            if (last_date.equals("")) {
                Constant.setString(activity, Constant.LAST_DATE, currentDate);
                Constant.addPoints(activity, Integer.parseInt(getResources().getString(R.string.daily_check_in_points)), 0);
                showDialogOfPoints(Integer.parseInt(getResources().getString(R.string.daily_check_in_points)));
                if (getActivity() == null) {
                    return;
                }
                ((MainActivity) getActivity()).setPointsText();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date pastDAte = sdf.parse(last_date);
                    Date currentDAte = sdf.parse(currentDate);
                    long diff = currentDAte.getTime() - pastDAte.getTime();
                    long difference_In_Days = (diff / (1000 * 60 * 60 * 24)) % 365;
                    Log.e("TAG", "onClick: Days Diffrernce" + difference_In_Days);
                    if (difference_In_Days > 0) {
                        Constant.setString(activity, Constant.LAST_DATE, currentDate);
                        Constant.addPoints(activity, Integer.parseInt(getResources().getString(R.string.daily_check_in_points)), 0);
                        showDialogOfPoints(Integer.parseInt(getResources().getString(R.string.daily_check_in_points)));
                        if (getActivity() == null) {
                            return;
                        }
                        ((MainActivity) getActivity()).setPointsText();
                    } else {
                        showDialogOfPoints(0);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Constant.showInternetErrorDialog(activity, getResources().getString(R.string.no_internet_connection));
        }
    }

    public void showDialogOfPoints(int points) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_points_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView imageView = dialog.findViewById(R.id.points_image);
        TextView title_text = dialog.findViewById(R.id.title_text_points);
        TextView points_text = dialog.findViewById(R.id.points);
        AppCompatButton add_btn = dialog.findViewById(R.id.add_btn);

        if (points == Integer.parseInt(getResources().getString(R.string.daily_check_in_points))) {
            imageView.setImageResource(R.drawable.congo);
            title_text.setText(getResources().getString(R.string.you_won));
            points_text.setVisibility(View.VISIBLE);
            points_text.setText(String.valueOf(points));
            add_btn.setText(getResources().getString(R.string.add_to_wallet));
        } else {
            imageView.setImageResource(R.drawable.donee);
            title_text.setText(getResources().getString(R.string.today_checkin_over));
            points_text.setVisibility(View.GONE);
            add_btn.setText(getResources().getString(R.string.okk));
        }
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void openPortal(){
        String url = this.getResources().getString(R.string.play_qureka);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.blue));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));}



//    public void qurekaopen(){
//        childReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String banner = dataSnapshot.getValue(String.class);
//
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.blue));
//                CustomTabsIntent customTabsIntent = builder.build();
//                customTabsIntent.intent.setPackage("com.android.chrome");
//                customTabsIntent.launchUrl(requireContext(), Uri.parse(banner));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseerror) {
//
//            }
//        });
//    }
}