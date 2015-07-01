package sg.com.bigspoon.www.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.BrandActivity;
import sg.com.bigspoon.www.data.StoryModel;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.STORY_LINK;

/**
 * Created by qiaoliang89 on 1/7/15.
 */
public class CardArrayAdapter extends RecyclerView.Adapter<CardArrayAdapter.PersonViewHolder> {

    class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        Button learnMoreButton;
        ImageView photo;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.person_name);
            learnMoreButton = (Button)itemView.findViewById(R.id.person_age);
            photo = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    ArrayList<StoryModel> storys;
    Context mContext;
    public CardArrayAdapter(ArrayList<StoryModel> storys, Context context){
        this.storys = storys;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brand_card, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
        personViewHolder.title.setText(storys.get(i).name);
        Ion.with(mContext).load(BASE_URL + storys.get(i).photo.thumbnailLarge).withBitmap().intoImageView(personViewHolder.photo);
        final String link = this.storys.get(i).url;
        personViewHolder.learnMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, BrandActivity.class);
                i.putExtra(STORY_LINK, link);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storys.size();
    }
}