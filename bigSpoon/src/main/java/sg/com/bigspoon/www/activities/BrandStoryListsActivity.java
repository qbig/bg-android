package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;
import sg.com.bigspoon.www.R;

/**
 * Created by qiaoliang89 on 1/6/15.
 */
public class BrandStoryListsActivity extends Activity {
    private CardListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        mListView = (CardListView) findViewById(R.id.card_list_base);
        initCards();
    }

    private void initCards() {

        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=0;i<200;i++){
            cards.add(getCard());
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(BrandStoryListsActivity.this, cards);

        if (mListView!=null){
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    private MaterialLargeImageCard getCard() {

        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        // Set supplemental actions
        TextSupplementalAction t1 = new TextSupplementalAction(BrandStoryListsActivity.this, R.id.text1);
        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(BrandStoryListsActivity.this," Click on Text SHARE ",Toast.LENGTH_SHORT).show();
            }
        });
        actions.add(t1);

        TextSupplementalAction t2 = new TextSupplementalAction(BrandStoryListsActivity.this, R.id.text2);
        t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(BrandStoryListsActivity.this," Click on Text LEARN ",Toast.LENGTH_SHORT).show();
            }
        });
        actions.add(t2);

        //Create a Card, set the title over the image and set the thumbnail
        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(BrandStoryListsActivity.this)
                        .setTextOverImage("Italian Beaches")
                        .setTitle("This is my favorite local beach")
                        .setSubTitle("A wonderful place")
                        .useDrawableId(R.drawable.biglogo)
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

    public class CardExample extends Card{

        protected String mTitleHeader;
        protected String mTitleMain;

        public CardExample(Context context,String titleHeader,String titleMain) {
            super(context, R.layout.carddemo_example_inner_content);
            this.mTitleHeader=titleHeader;
            this.mTitleMain=titleMain;
            init();
        }

        private void init(){

            //Create a CardHeader
            CardHeader header = new CardHeader(BrandStoryListsActivity.this);

            //Set the header title
            header.setTitle(mTitleHeader);

            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(R.menu.select_action_menu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    Toast.makeText(BrandStoryListsActivity.this, "Click on card menu" + mTitleHeader + " item=" + item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            addCardHeader(header);

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getContext(), "Click Listener card=" + mTitleHeader, Toast.LENGTH_SHORT).show();
                }
            });

            //Set the card inner text
            setTitle(mTitleMain);
        }

    }
}
