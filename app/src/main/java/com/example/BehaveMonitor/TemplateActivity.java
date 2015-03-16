package com.example.BehaveMonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//Templates are saved as .template files.
//Templates are stored in the format "TemplateName;BehaviourName1,BehaviourType1:BehaviourName2,BehaviourType2... "



public class TemplateActivity extends Activity {

    private String startTemp; // Used to check for changes
	private Template newTemp = new Template();
    private boolean fromFragment = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_template);

		setAddBehaviour();
		setSaveTemplate();

        Template template = getIntent().getParcelableExtra("template");
        if (template != null) {
            newTemp = template;
            updateBehaviours();
        }

        fromFragment = getIntent().getBooleanExtra("fromFragment", false);

        startTemp = newTemp.toString();
	}

    private boolean changed() {
        if (newTemp != null && startTemp != null) {
            if (newTemp.toString().equals(startTemp)) {
                return false;
            }
        }

        return true;
    }

	// Sets up the button for adding a behaviour
	public void setSaveTemplate() {
		ImageButton button = (ImageButton) findViewById(R.id.save_template);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				//if the template isnt empty save it;
//				if (!newTemp.isEmpty()) saveTemplate();
                if (changed()) {
                    saveTemplate();
                }
			}
		});
	}
	
	public void saveTemplate() {
        if ("null;".equals(startTemp)) {
            showNamingDialog();
        } else {
            showOverwriteDialog();
        }
	}

    private void showNamingDialog() {
        //Check to save
        final Context context = TemplateActivity.this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_template, null);
        final EditText input = (EditText) dialogView.findViewById(R.id.dialog_template_name);
        alert.setView(dialogView);

        //If yes start save.
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String template = input.getText().toString();
                if (template.contains(",")) {
                    makeSomeToast("Invalid template name");
                    return;
                }

                newTemp.name = template;
                if (FileHandler.templateExists(template)) {
                    dialog.dismiss();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setTitle("Overwrite Template");
                    alert.setMessage("A template with this name already exists, do you want to overwrite it?");

                    // If yes overwrite
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FileHandler.saveTemplate(TemplateActivity.this, newTemp);
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
                    dialog.dismiss();
                    newTemp.name = template;
                    FileHandler.saveTemplate(context, newTemp);
                    backToMain();
                }
            }
        });
    }

    private void showOverwriteDialog() {
        final Context context = TemplateActivity.this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_overwrite_template, null);
        alert.setView(dialogView);

        //If yes start save.
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FileHandler.saveTemplate(TemplateActivity.this, newTemp);
                backToMain();
            }
        });

        alert.setNeutralButton("Save as..", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showNamingDialog();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

	public void backToMain() {
        Intent intent = new Intent(TemplateActivity.this, HomeActivity.class);
        if (fromFragment) {
            intent.putExtra("redirect", 2);
        }

        intent.putExtra("activeTemplateString", newTemp.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!changed()) {
            backToMain();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save before exit?");
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTemplate();
            }
        });

        alert.setNeutralButton("Don't save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                backToMain();
            }
        });

        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    //re-add behaviours to layout
	//cycles through the behaviours in newTemp and adds them to the layout
	public void updateBehaviours() {
		LinearLayout bLayout = (LinearLayout) findViewById(R.id.behaviour_layout);
		if (bLayout.getChildCount() > 0) bLayout.removeAllViews();
		ArrayList<Behaviour> behaviours = newTemp.behaviours;
		int numOfBs = newTemp.behaviours.size();

		if (numOfBs > 0) {
			//go through behaviours adding them to the layout.
			for(int i = 0; i < numOfBs; i++) {
				TextView tv = new TextView(TemplateActivity.this);
				tv.setText(behaviours.get(i).bName);
				tv.setPadding(5, 2, 5, 2);
				tv.setTextSize(getResources().getDimension(R.dimen.textsize));
//				if(i%2==0){
//					int dark = Color.parseColor("#B3E5FC");
//					tv.setBackgroundColor(dark);
//				} else {
//					int light = Color.parseColor("#E1F5FE");
//					tv.setBackgroundColor(light);
//				}

				final int ii = i;
				OnClickListener ocl = new OnClickListener() {
				    @Override
				    public void onClick(View v) {
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
	
	public void newBehaviour(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_behaviour, null);

        TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_behaviour_title);
        titleText.setText("New Behaviour");

        final EditText bName = (EditText) dialogView.findViewById(R.id.dialog_behaviour_name);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.behaviour_type_spinner);
        String[] spinnerArray = new String[]{"State","Event"};
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);

        alert.setView(dialogView);

		alert.setPositiveButton("Ok", null);
	
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

        final AlertDialog dialog = alert.create();
		dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = bName.getText().toString();
                if (!name.contains(",") && !name.contains(";") && !name.contains(":")) {
                    Behaviour b = new Behaviour();
                    b.bName = name;
                    b.type = spinner.getSelectedItemPosition();
                    newTemp.behaviours.add(b);
                    updateBehaviours();
                    dialog.dismiss();
                } else {
                    makeSomeToast("Invalid behaviour name");
                }
            }
        });
	}
	
	//Brings up the dialog box with added remove button as well as info about behaviour.
	public void editBehaviour(final Context context, final int index) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_behaviour, null);

        TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_behaviour_title);
        titleText.setText("Edit Behaviour");

        final EditText bName = (EditText) dialogView.findViewById(R.id.dialog_behaviour_name);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.behaviour_type_spinner);
        String[] spinnerArray = new String[]{"State","Event"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        // get the current behaviour
        Behaviour currentBehaviour = newTemp.behaviours.get(index);
		bName.setText(currentBehaviour.bName);
        spinner.setSelection(currentBehaviour.type);

		alert.setView(dialogView);
		
		alert.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		    	newTemp.behaviours.remove(index);
		    	updateBehaviours();
			}
        });

		alert.setPositiveButton("Ok", null);
	
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

        final AlertDialog dialog = alert.create();
		dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = bName.getText().toString();
                if (!name.contains(",") && !name.contains(";") && !name.contains(":")) {
                    newTemp.behaviours.get(index).bName = name;
                    newTemp.behaviours.get(index).type = spinner.getSelectedItemPosition();

                    updateBehaviours();
                    dialog.dismiss();
                } else {
                    makeSomeToast("Invalid behaviour name");
                }
            }
        });
	}

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
	