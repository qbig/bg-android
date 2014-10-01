package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;



public class WaterQuantityPickerAdapter extends LinearLayout {

	private final long REPEAT_DELAY = 50;
	
	private final int ELEMENT_HEIGHT = 60;
	private final int ELEMENT_WIDTH = ELEMENT_HEIGHT; // these two parameters control the size of the two buttons in the layout
	
	private final int MINIMUM = 0;
	private final int MAXIMUM = 999;
	
	private final int TEXT_SIZE = 17;
	
	public Integer value;
	
	ImageButton decrement;
	ImageButton increment;
	public TextView valueText;
	
	private Handler repeatUpdateHandler = new Handler();
	
	private boolean autoIncrement = false;
	private boolean autoDecrement = false;

	class RepetetiveUpdater implements Runnable {
		public void run() {
			if( autoIncrement ){
				increment();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			} else if( autoDecrement ){
				decrement();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			}
		}
	}
	
	public WaterQuantityPickerAdapter( Context context, AttributeSet attributeSet ) {
		super(context, attributeSet);
		
		this.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) );
		LayoutParams elementParams = new LinearLayout.LayoutParams( ELEMENT_HEIGHT, ELEMENT_WIDTH );
		
		// init the individual elements
		initDecrementButton( context );
		initValueEditText( context );
		initIncrementButton( context );
		
		// Can be configured to be vertical or horizontal
		// Thanks for the help, LinearLayout!	
		if( getOrientation() == VERTICAL ){
			addView( increment, elementParams );
			addView( valueText, elementParams );
			addView( decrement, elementParams );
		} else {
			addView( decrement, elementParams );
			addView( valueText, elementParams );
			addView( increment, elementParams );
		}
	}
	
	private void initIncrementButton( Context context){
		increment = new ImageButton( context );
		increment.setBackgroundResource(R.drawable.add_btn_with_circle);
		//increment.setLayoutParams(new LinearLayout.LayoutParams(10, 100));
	
		
		// Increment once for a click
		increment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	increment();
            }
        });
		
		// Auto increment for a long click
		increment.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoIncrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto incrementing, stop
		increment.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoIncrement ){
					autoIncrement = false;
				}
				return false;
			}
		});
	}
	
	private void initValueEditText( Context context){
		
		value = new Integer( 0 );
		
		valueText = new TextView( context );
		valueText.setTextSize( TEXT_SIZE );
		Color.parseColor("#bdbdbd");
		valueText.setTextColor(Color.parseColor("#bdbdbd"));
		
		// Since we're a number that gets affected by the button, we need to be
		// ready to change the numeric value with a simple ++/--, so whenever
		// the value is changed with a keyboard, convert that text value to a
		// number. We can set the text area to only allow numeric input, but 
		// even so, a carriage return can get hacked through. To prevent this
		// little quirk from causing a crash, store the value of the internal
		// number before attempting to parse the changed value in the text area
		// so we can revert to that in case the text change causes an invalid
		// number
		valueText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int arg1, KeyEvent event) {
				int backupValue = value;
				try {
					value = Integer.parseInt( ((EditText)v).getText().toString() );
				} catch( NumberFormatException nfe ){
					value = backupValue;
				}
				return false;
			}
		});
		
		// Highlight the number when we get focus
		valueText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ){
					((EditText)v).selectAll();
				}
			}
		});
		valueText.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL );
		valueText.setText( value.toString() );
	}
	
	
	private void initDecrementButton( Context context){
		decrement = new ImageButton( context );
		decrement.setBackgroundResource(R.drawable.btn_minus_with_circle);
		

		// Decrement once for a click
		decrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrement();
            }
        });
		

		// Auto Decrement for a long click
		decrement.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoDecrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto decrementing, stop
		decrement.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoDecrement ){
					autoDecrement = false;
				}
				return false;
			}
		});
	}
	
	public void increment(){
		if( value < MAXIMUM ){
			value = value + 1;
			valueText.setText( value.toString() );
		}
	}

	public void decrement(){
		if( value > MINIMUM ){
			value = value - 1;
			valueText.setText( value.toString() );
		}
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue( int value ){
		if( value > MAXIMUM ) value = MAXIMUM;
		if( value >= 0 ){
			this.value = value;
			valueText.setText( this.value.toString() );
		}
	}
	
}
