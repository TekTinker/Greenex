package com.greenexagro.greenex.NavigationFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.greenexagro.greenex.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUs extends Fragment {

    private View view;
    private WebView webView;
    private Button btnSendMail;


    public ContactUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        btnSendMail = (Button) view.findViewById(R.id.btContactUs);

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "support@greenexagro.com" });
                email.putExtra(Intent.EXTRA_SUBJECT, "Contact");

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Send Email"));
            }
        });

        webView = (WebView) view.findViewById(R.id.wbvContactUs);

        webView.loadData("<div style=\"margin: 15px; font-family: Raleway, sans-serif;\">\n" +
                "                                <h3>Registered & Corporate Office</h3>\n" +
                "                                <p>\n" +
                "                                    Greenex Agro Chemicals<br>\n" +
                "                                    Gut 45 At Shahajatpur Post Lasurgaon,<br>\n" +
                "                                    Taluka Vaijapur,<br>\n" +
                "                                    District Aurangabad,<br>\n" +
                "                                    Pin : 423701<br>\n" +
                "                                    Mobile : 9665101095<br>\n" +
                "                                    E-mail : support@greenexagro.com<br>\n" +
                "                                    Website : www.greenexagro.com<br><br>\n" +
                "                                </p>\n" +
                "                                <h3>Office Address</h3>\n" +
                "                                <p>\n" +
                "                                    Sandeep Krushi Seva Kendra,<br>\n" +
                "                                    Lasur station,<br>\n" +
                "                                    Taluka Gangapur,<br>\n" +
                "                                    District Aurangabad,\n" +
                "                                    Pin : 431133<br>\n" +
                "                                    Telephone : 02433241595\n" +
                "                                </p>\n" +
                "                            </div>" , "text/html; charset=UTF-8", null);

        return view;
    }

}
