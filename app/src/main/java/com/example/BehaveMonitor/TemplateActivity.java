//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.adapters.BehaviourListAdapter;

//Templates are saved as .template files.
//Templates are stored in the format "TemplateName;BehaviourName1,BehaviourType1:BehaviourName2,BehaviourType2... "



public class TemplateActivity extends Activity {

    private BehaviourListAdapter adapter;

    private String startTemp; // Used to check for changes
	private Template newTemp = new Template();
    private boolean fromFragment = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_template);

        Template template = getIntent().getParcelableExtra("template");
        if (template != null) {
            newTemp = template;
//            updateBehaviours();
        }

		initAddBehaviour();
        initList();
		initSaveTemplate();

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

    /**
     * Initialises the list view
     */
    private void initList() {
        adapter = new BehaviourListAdapter(this, newTemp.behaviours);

        ListView list = (ListView) findViewById(R.id.template_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Behave", "Item clicked " + position);
                editBehaviour(TemplateActivity.this, position);
            }
        });
    }

	// Sets up the button for adding a behaviour
	private void initSaveTemplate() {
		ImageButton button = (ImageButton) findViewById(R.id.save_template);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Only save the template if it has changed.
                if (changed()) {
                    saveTemplate();
                }
			}
		});
	}
	
	private void saveTemplate() {
        if ("null;".equals(startTemp)) {
            showNamingDialog();
        } else {
            showOverwriteDialog();
        }
	}

    private void showNamingDialog() {
        final Context context = TemplateActivity.this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_template, null);
        final EditText input = (EditText) dialogView.findViewById(R.id.dialog_template_name);
        alert.setView(dialogView);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Deliberately left blank as we override the button later.
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
                String template = input.getText().toString().trim();
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

	private void backToMain() {
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
//	private void updateBehaviours() {
//		LinearLayout bLayout = (LinearLayout) findViewById(R.id.behaviour_layout);
//		if (bLayout.getChildCount() > 0) bLayout.removeAllViews();
//		ArrayList<Behaviour> behaviours = newTemp.behaviours;
//		int numOfBs = newTemp.behaviours.size();
//
//		if (numOfBs > 0) {
//			//go through behaviours adding them to the layout.
//			for(int i = 0; i < numOfBs; i++) {
//				TextView tv = new TextView(TemplateActivity.this);
//				tv.setText(behaviours.get(i).bName);
//				tv.setPadding(5, 2, 5, 2);
//				tv.setTextSize(getResources().getDimension(R.dimen.textsize));
////				if(i%2==0){
////					int dark = Color.parseColor("#B3E5FC");
////					tv.setBackgroundColor(dark);
////				} else {
////					int light = Color.parseColor("#E1F5FE");
////					tv.setBackgroundColor(light);
////				}
//
//				final int ii = i;
//				OnClickListener ocl = new OnClickListener() {
//				    @Override
//				    public void onClick(View v) {
//				    	editBehaviour(TemplateActivity.this, ii );
//				    }
//				};
//
//				tv.setOnClickListener(ocl);
//				bLayout.addView(tv);
//			}
//		}
//
//	}

	//Sets up the button for adding a behaviour
	private void initAddBehaviour() {
		ImageButton button = (ImageButton) findViewById(R.id.new_behaviour);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newBehaviour();
			}
		});
	}
	
	private void newBehaviour() {
        Context context = this;

        //TODO: Change to not use custom view.
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context);
        dialog.title("New Behaviour");
        dialog.customView(R.layout.dialog_behaviour, true);
        dialog.negativeText("Cancel");
        dialog.positiveText("Ok");
        dialog.show();

//		AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_behaviour, null);
//
//        TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_behaviour_title);
//        titleText.setText("New Behaviour");
//
//        final EditText bName = (EditText) dialogView.findViewById(R.id.dialog_behaviour_name);
//        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.behaviour_type_spinner);
//        String[] spinnerArray = new String[]{"State","Event"};
//		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
//		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner.setAdapter(spinnerArrayAdapter);
//
//        alert.setView(dialogView);
//
//		alert.setPositiveButton("Ok", null);
//
//		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//		  public void onClick(DialogInterface dialog, int whichButton) {
//		    // Canceled.
//		  }
//		});
//
//        final AlertDialog dialog = alert.create();
//		dialog.show();
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String name = bName.getText().toString().trim();
//                if (!name.contains(",") && !name.contains(";") && !name.contains(":")) {
//                    Behaviour b = new Behaviour();
//                    b.setName(name);
//                    b.setType(spinner.getSelectedItemPosition());
//                    newTemp.behaviours.add(b);
////                    updateBehaviours();
////                    adapter.addBehaviour(b);
//
//                    dialog.dismiss();
//                } else {
//                    makeSomeToast("Invalid behaviour name");
//                }
//            }
//        });
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
		bName.setText(currentBehaviour.getName());
        spinner.setSelection(currentBehaviour.getType());

		alert.setView(dialogView);
		
		alert.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		    	newTemp.behaviours.remove(index);
		    	adapter.refresh();
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
                String name = bName.getText().toString().trim();
                if (!name.contains(",") && !name.contains(";") && !name.contains(":")) {
                    newTemp.behaviours.get(index).setName(name);
                    newTemp.behaviours.get(index).setType(spinner.getSelectedItemPosition());

//                    adapter.replace()
                    adapter.refresh();
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
	