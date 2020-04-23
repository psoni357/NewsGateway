package com.paavansoni.newsgateway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {

    //private static final String TAG = "ArticleFragment";

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @param index Parameter 2.
     * @param max Parameter 3.
     * @return A new instance of fragment ArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleFragment newInstance(Article article, int index, int max) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARTICLE_DATA", article);
        args.putInt("INDEX", index);
        args.putInt("MAX", max);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);
        Bundle args = getArguments();
        if (args != null) {
            final Article currentArticle = (Article)args.getSerializable("ARTICLE_DATA");
            int index = args.getInt("INDEX");
            int max = args.getInt("MAX");

            TextView title = fragment_layout.findViewById(R.id.title);
            if(currentArticle.getTitle()==null || currentArticle.getTitle().equals("null")){
                title.setVisibility(View.INVISIBLE);
            }
            else{
                title.setVisibility(View.VISIBLE);
                title.setText(currentArticle.getTitle());
            }

            TextView author = fragment_layout.findViewById(R.id.Author);
            if(currentArticle.getAuthor()==null || currentArticle.getAuthor().equals("null")){
                author.setVisibility(View.INVISIBLE);
            }
            else{
                author.setVisibility(View.VISIBLE);
                author.setText(currentArticle.getAuthor());
            }

            TextView date = fragment_layout.findViewById(R.id.Published);
            if(currentArticle.getPublishedAt()==null || currentArticle.getPublishedAt().equals("null")){
                date.setVisibility(View.INVISIBLE);
            }
            else{
                date.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date d = sdf.parse(currentArticle.getPublishedAt());
                    date.setText(d.toString());
                } catch (ParseException e) {
                    date.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }
            }

            TextView description = fragment_layout.findViewById(R.id.Description);
            if(currentArticle.getDescription()==null || currentArticle.getDescription().equals("null")){
                description.setVisibility(View.INVISIBLE);
            }
            else{
                description.setVisibility(View.VISIBLE);
                description.setText(currentArticle.getDescription());
            }

            ImageView pic = fragment_layout.findViewById(R.id.ArticlePhoto);
            Picasso picasso = new Picasso.Builder(container.getContext()).build();
            picasso.load(currentArticle.getImageUrl())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pic);

            pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPic(currentArticle.getUrl());
                }
            });

            TextView idx = fragment_layout.findViewById(R.id.Index);
            idx.setText("" + index + " of " + max);
            return fragment_layout;
        }
        else{
            return null;
        }

    }

    private void clickPic(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
