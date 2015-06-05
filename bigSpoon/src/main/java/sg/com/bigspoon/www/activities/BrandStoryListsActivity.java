package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.BRAND_WAKE_UP_SIGNAL;
import static sg.com.bigspoon.www.data.Constants.STORY_LINK;
/**
 * Created by qiaoliang89 on 1/6/15.
 */
public class BrandStoryListsActivity extends Activity {
    private CardListView mListView;
    private ArrayList<CardInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        mListView = (CardListView) findViewById(R.id.card_list_base);
        initData();
        initCards();
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(User.getInstance(this).currentOutlet.name + " Story");

        if (getIntent().getBooleanExtra(BRAND_WAKE_UP_SIGNAL, false)){
            unlockScreen();
        }
    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void initCards() {

        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=0; i<4 ;i++){
            cards.add(getCard(i));
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(BrandStoryListsActivity.this, cards);

        if (mListView!=null){
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    private MaterialLargeImageCard getCard(int num) {

        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
        final CardInfo info = this.data.get(num);
        TextSupplementalAction t2 = new TextSupplementalAction(BrandStoryListsActivity.this, R.id.text2);
        t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(BrandStoryListsActivity.this, BrandActivity.class);
                i.putExtra(STORY_LINK, info.link);
                BrandStoryListsActivity.this.startActivity(i);
            }
        });
        actions.add(t2);

        //Create a Card, set the title over the image and set the thumbnail
        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(BrandStoryListsActivity.this)
                        .setTextOverImage(info.imageText)
                        .setTitle(info.title)
                        .setSubTitle(info.subtitle)
                        .useDrawableId(info.drawableId)
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

    private void initData(){
        this.data = new ArrayList<CardInfo>();
        this.data.add(new CardInfo(null, "Title is kind of long. But Okay.", "This is a SubTitle. Put Subtile here. A bit more text", R.drawable.rsz_1biglogo, "http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square"));
        this.data.add(new CardInfo("Our Story", null, "This is a SubTitle. Put Subtile here. A bit more text", R.drawable.rsz_food, "http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square"));
        this.data.add(new CardInfo("Our Story", "Title is kind of long. But Okay.", null, R.drawable.rsz_yh_justin, "http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square"));
        this.data.add(new CardInfo("Our Story", "Title is kind of long. But Okay.", "This is a SubTitle. Put Subtile here. A bit more text", R.drawable.rsz_photo_wall, "http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square"));
    }

    class CardInfo {
        String imageText;
        String title;
        String subtitle;
        int drawableId;
        String link;
        CardInfo(String imageText,
                String title,
                String subtitle,
                int drawableId,
                String link) {
            this.imageText = imageText;
            this.title = title;
            this.subtitle = subtitle;
            this.drawableId = drawableId;
            this.link = link;
        }
    }
}
