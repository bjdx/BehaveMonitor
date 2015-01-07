package com.example.BehaveMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

//Templates are saved as .tmp files.
//Templates are stored in the format "TemplateName;BehaviourName1,BehaviourType1:BehaviourName2,BehaviourType2... "



public class TemplateActivity extends Activity {
	public int which = -1;
	Template newTemp = new Template();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_template);
		setAddBehaviour();
		setSaveTemplate();
	}
	
	//Sets up the button for adding a behaviour
	public void setSaveTemplate() {
		ImageButton button = (ImageButton) findViewById(R.id.save_template);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//if the template isnt empty save it;
				if(!newTemp.isEmpty())saveTemplate(v);
			}
		});
	}
	
	public void saveTemplate(final View rootView) {

        //Check to save
        final Context context = TemplateActivity.this;
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		
		alert.setTitle("Save Template");
		alert.setMessage("What do you want to save the template as:");
		final EditText input = new EditText(context);
		alert.setView(input);


        //If yes start save.
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			final String tmpName = input.getText().toString();
			newTemp.name = tmpName;
		  	String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "Templates" + File.separator;
			final File file = new File(dirPath + tmpName+".tmp");

            //Check if the template already exists.
            if(file.exists()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                alert.setTitle("Overwrite Template");
                alert.setMessage("Warning a template with this name already exists, do you want to overwrite it?");

                //If yes overwrite
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //Convert template to string the write it and read it back to check.
                        String string = newTemp.toString();

                        //Save File
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(string.getBytes());
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        //Read back file and check against original.
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            byte[] data = new byte[(int)file.length()];

                            fis.read(data);
                            String tmpIN = new String(data,"UTF-8");
                            Log.d("Read Back Template:", tmpIN);
                            Log.d("Template Saved Correctly:",""+tmpIN.equals(string));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Set template name and go back to select template
                        backToMain();

                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

            } else {
                //Convert template to string the write it and read it back to check.
                String string = newTemp.toString();

                //Save File
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(string.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                //Read back file and check against original.
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int)file.length()];

                    fis.read(data);
                    String tmpIN = new String(data,"UTF-8");
                    Log.d("Read Back Template:", tmpIN);
                    Log.d("Template Saved Correctly:",""+tmpIN.equals(string));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                backToMain();
            }
		  }
		});
	
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
	
		alert.show();
	}
	
	public void backToMain() {
        Intent intent = new Intent(TemplateActivity.this, MainActivity.class);
        intent.putExtra("result",newTemp.name);
        setResult(RESULT_OK,intent);
        finish();
    }

	//re-add behaviours to layout
	//cycles through the behaviours in newTemp and adds them to the layout
	public void updateBehaviours() {
		LinearLayout bLayout = (LinearLayout) findViewById(R.id.behaviour_layout);
		if(bLayout.getChildCount() > 0) bLayout.removeAllViews();
		ArrayList<Behaviour> behaviours = newTemp.behaviours;
		int numOfBs = newTemp.behaviours.size();
		
		
		if (numOfBs > 0) {
			//go through behaviours adding them to the layout.
			for(int i = 0; i < numOfBs; i++) {
				TextView tv = new TextView(TemplateActivity.this);
				tv.setText(behaviours.get(i).bName);
				tv.setPadding(5, 2, 5, 2);
				tv.setTextSize(getResources().getDimension(R.dimen.textsize));
				if(i%2==0){
					int dark = Color.parseColor("#B3E5FC");
					tv.setBackgroundColor(dark);
				} else {
					int light = Color.parseColor("#E1F5FE");
					tv.setBackgroundColor(light);
				}
				final int ii = i;
				OnClickListener ocl = new OnClickListener() {
				    @Override
				    public void onClick(View v) {
				    	TextView tv = (TextView) v;
				    	tv.getText().toString();
				    	editBehaviour(TemplateActivity.this, ii );
				    	
				    }
				};
				
				tv.setOnClickListener(ocl);
				bLayout.addView(tv);
			}
		}
		
	}
	
	
	//Sets up the button for adding a behaviour
	public void setAddBehaviour() {
		ImageButton button = (ImageButton) findViewById(R.id.new_behaviour);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				newBehaviour(TemplateActivity.this);
			}
			
		
		});
	}
	
	
	
	//code thats called when the add new behaviour button is tapped.
	public void newBehaviour(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
	
		alert.setTitle("Behaviour Editor");
		alert.setMessage("Specify behaviour name and type:");
		
		 LinearLayout layout = new LinearLayout(this);
		 layout.setOrientation(LinearLayout.VERTICAL);
		 layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		 

		
		// Set an EditText view to get user input 
		final EditText bName = new EditText(context);
		layout.addView(bName);
		
		
		final Spinner spinner = new Spinner(context);
		String[] spinnerArray = new String[]{"Event","State"};
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);

		layout.addView(spinner);
		alert.setView(layout);
		
        
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			//addBehaviour();
			//saveBehaviour(false, bName.getText().toString(), spinner.getSelectedItemPosition());

			Behaviour b = new Behaviour();
			b.bName = bName.getText().toString();
			b.type = spinner.getSelectedItemPosition();
			newTemp.behaviours.add(b);
			updateBehaviours();
		}
		   
		});
	
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
	
		alert.show();
	}
	
	//Brings up the dialog box with added remove button as well as info about behaviour.
	public void editBehaviour(final Context context, final int index) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
	
		alert.setTitle("Behaviour Editor");
		alert.setMessage("Specify behaviour name and type:");
		
		 LinearLayout layout = new LinearLayout(this);
		 layout.setOrientation(LinearLayout.VERTICAL);
		 layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		 
		 //get the current behaviour 
		 Behaviour currentBehaviour = newTemp.behaviours.get(index);
		
		// Set an EditText view to get user input 
		final EditText bName = new EditText(context);
		bName.setText(currentBehaviour.bName);
		layout.addView(bName);
		
		
		final Spinner spinner = new Spinner(context);
		String[] spinnerArray = new String[]{"Event","State"};
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		if(currentBehaviour.type == 0) {
			spinner.setSelection(0);
		} else {
			spinner.setSelection(1);
		}
		layout.addView(spinner);
		
		
		
		alert.setView(layout);
		
		alert.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		    	newTemp.behaviours.remove(index);
		    	//ADD TOAST TO SAY REMOVED
		    	
		    	updateBehaviours();
		    	
			}
			   
			});
		
        
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			//addBehaviour();
			newTemp.behaviours.get(index).bName = bName.getText().toString();
			Log.e("Selected item was:", ""+spinner.getSelectedItemPosition());
			newTemp.behaviours.get(index).type = spinner.getSelectedItemPosition();

			//incase the names changeS
	    	updateBehaviours();
		}
		   
		});
	
		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
	
		alert.show();
	}
	
}
	