package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.BigSpoon;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.StoryModel;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.BRAND_WAKE_UP_SIGNAL;
import static sg.com.bigspoon.www.data.Constants.STORY_LINK;
/**
 * Created by qiaoliang89 on 1/6/15.
 */
public class BrandStoryListsActivity extends Activity {
    private CardListView mListView;
    private StoryModel[] storys;
    private Integer[] storySequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        mListView = (CardListView) findViewById(R.id.card_list_base);
        try {
            storys = User.getInstance(this).currentOutlet.storys;
            storySequence = User.getInstance(this).currentOutlet.storySequence;
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            Log.i(BrandStoryListsActivity.class.toString(), npe.getMessage());
        }

        initCards();
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(User.getInstance(this).currentOutlet.name + " Story");

        if (getIntent().getBooleanExtra(BRAND_WAKE_UP_SIGNAL, false)){
            unlockScreen();
            Intent i = new Intent(BrandStoryListsActivity.this, BrandActivity.class);
            i.putExtra(STORY_LINK, "http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square");
            BrandStoryListsActivity.this.startActivity(i);
        }
    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ((BigSpoon)getApplicationContext()).wakeDevice();
    }

    private int getStory(int id) {
        for (int i=0; i< storys.length ;i++){
            if (storys[i].storyId == id) {
                return i;
            }
        }
        return 0;
    }

    private void initCards() {

        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=0; i< storySequence.length; i++){
            cards.add(getCard(getStory(storySequence[i])));
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(BrandStoryListsActivity.this, cards);

        if (mListView!=null){
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    private MaterialLargeImageCard getCard(int num) {

        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
        final StoryModel info = this.storys[num];
        TextSupplementalAction actionButton = new TextSupplementalAction(BrandStoryListsActivity.this, R.id.text2);
        actionButton.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(BrandStoryListsActivity.this, BrandActivity.class);
                i.putExtra(STORY_LINK, info.url);
                BrandStoryListsActivity.this.startActivity(i);
            }
        });
        actions.add(actionButton);

        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(BrandStoryListsActivity.this)
                        //.setTextOverImage(info.imageText)
                        .setTitle(info.name)
                        //.setSubTitle(info.subtitle)
                        .useDrawableUrl(Constants.BASE_URL + info.photo.thumbnailLarge)
                        //.useDrawableId(R.id.)
                        .setupSupplementalActions(R.layout.material_card_action, actions)
                        .build();

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(BrandStoryListsActivity.this," Click on ActionArea ",Toast.LENGTH_SHORT).show();
            }
        });
        return card;
    }
}
