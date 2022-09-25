package com.greenexagro.greenex.NavigationFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.greenexagro.greenex.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    private View view;
    private WebView webView;


    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_about_us, container, false);

        webView = (WebView) view.findViewById(R.id.wbvAboutUs);

        webView.loadData( "\n" +
                "                        <div class=\"col-md-8\">\n" +
                "                            <div style=\"margin: 5px; font-family: Raleway, sans-serif;\">\n" +
                "                                <p style=\"margin-top: 20px; margin-bottom: 20px; text-align: justify;font-size: medium\">\n" +
                "\n" +
                "                                    Greenex Agro Chemicals belongs to the fertilizers sector of India and was\n" +
                "                                    incorporated\n" +
                "                                    in 2014.\n" +
                "                                    It forms an important private sector fertilizer industry in India.\n" +
                "                                    <br><br>\n" +
                "                                    The company’s manufacturing plant is situated at village Shahajatpur in Maharashtra\n" +
                "                                    Fertilizers have helped in increasing the agricultural produce of the country, for\n" +
                "                                    they\n" +
                "                                    have elements which increase the growth of the crops.\n" +
                "                                    <br><br>\n" +
                "                                    Greenex Agro Chemicals has a major role to play in boosting not only the performance\n" +
                "                                    of\n" +
                "                                    India’s agricultural sector but also the economy as well.\n" +
                "                                    <br><br>\n" +
                "                                    Greenex Agro Chemicals has a wide experience of providing Bio-fertilizers, Micro and\n" +
                "                                    Macro Nutrients that are recognized as ideal organic products, commercially viable\n" +
                "                                    for\n" +
                "                                    Organic Farming worldwide, focusing on sustainable agriculture & balanced nutrient\n" +
                "                                    supply to plants.\n" +
                "                                    <br><br>\n" +
                "                                    Greenex Agro Chemicals products have superior performance and relatively low cost\n" +
                "                                    when\n" +
                "                                    compared to similar Agro products. Our other Services include Soil Testing, Farmer\n" +
                "                                    Consultation, Greenhouse/Playhouse farming Services.\n" +
                "                                    <br><br>\n" +
                "                                    Greenex Agro Chemicals target customers are primarily farmers. Growth drivers are\n" +
                "                                    the\n" +
                "                                    intensive marketing network penetrating even the interiors of India, increased farm\n" +
                "                                    income, enhanced awareness about the cost-benefit tradeoff of agro-chemicals, highly\n" +
                "                                    diverse product range with solution for almost all problems in all crops, innovative\n" +
                "                                    marketing strategies and international technical tie-ups. The Company keeps adding\n" +
                "                                    new\n" +
                "                                    products every year through its collaborations and is continuously on the lookout to\n" +
                "                                    bring the latest technology to Indian Farmers.\n" +
                "\n" +
                "                                </p>\n" +
                "\n" +
                "                                <br>\n" +
                "                                <br>\n" +
                "\n" +
                "                                <h3>Why choose us ?</h3>\n" +
                "                                <p style=\"margin-top: 20px; margin-bottom: 20px; text-align: justify;font-size: medium\">\n" +
                "                                    We offer not only a wide range of agricultural products but also professional\n" +
                "                                    approach and\n" +
                "                                    knowledge of international and local markets helps us to establish a good will in\n" +
                "                                    the\n" +
                "                                    international market as well as in the national market.\n" +
                "                                    We have a very efficient and experienced management team which provides total\n" +
                "                                    customer\n" +
                "                                    satisfaction through:\n" +
                "                                </p>\n" +
                "                                <ul style=\"text-align: justify;margin-right: 20px;font-size: medium\">\n" +
                "                                    <li>Listening to what our customer want because this is the only way to shed old\n" +
                "                                        ideas\n" +
                "                                        for new ones.\n" +
                "                                    </li>\n" +
                "                                    <li>Continuous improvement in quality and service.</li>\n" +
                "                                    <li>Timely delivery.</li>\n" +
                "                                    <li>Dedication, involvement and co-operation of all people at every stage.</li>\n" +
                "                                    <li>Balancing pre and post sales service.</li>\n" +
                "                                </ul>\n" +
                "                            </div>\n" +
                "                        </div>" , "text/html; charset=UTF-8", null);

        return view;
    }

}
