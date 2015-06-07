//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.adapters.BehaviourListAdapter;

//Templates are saved as .template files.
//Templates are stored in the format "TemplateName;BehaviourName1,BehaviourType1,Behaviour1Special:BehaviourName2,BehaviourType2,Behaviour2Special... "



public class TemplateActivity extends ActionBarActivity {

    private BehaviourListAdapter adapter;

    private String startTemp; // Used to check for changes
	private Template newTemp = new Template();
    private boolean fromFragment = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

		setContentView(R.layout.activity_template);

        Template template = getIntent().getParcelableExtra("template");
        if (template != null) {
            newTemp = template;
        }

        initList();
		initAddBehaviour();
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

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Save Template")
                .autoDismiss(false)
                .customView(R.layout.dialog_save_template, true)
                .negativeText("Cancel")
                .positiveText("Save")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        if (view == null) {
                            return;
                        }

                        EditText input = (EditText) view.findViewById(R.id.dialog_template_name);
                        String template = input.getText().toString().trim();
                        if ("".equals(template) || template.contains(",")) {
                            makeSomeToast("Invalid template name");
                            return;
                        }

                        newTemp.name = template;
                        if (FileHandler.templateExists(template)) {
                            dialog.dismiss();

                            new MaterialDialog.Builder(context)
                                    .title("Overwrite Template?")
                                    .content("A template with this name already exists, do you want to overwrite it?")
                                    .positiveText("Yes")
                                    .negativeText("Cancel")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            FileHandler.saveTemplate(TemplateActivity.this, newTemp);
                                            backToMain();
                                        }
                                    })
                                    .show();
                        } else {
                            dialog.dismiss();
                            newTemp.name = template;
                            FileHandler.saveTemplate(context, newTemp);
                            backToMain();
                        }
                    }
                })
                .build();

        dialog.show();
    }

    private void showOverwriteDialog() {
        new MaterialDialog.Builder(this)
                .title("Overwrite?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .neutralText("Save as..")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FileHandler.saveTemplate(TemplateActivity.this, newTemp);
                        backToMain();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        showNamingDialog();
                    }
                })
                .show();
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

        new MaterialDialog.Builder(this)
                .title("Save before exit?")
                .positiveText("Save")
                .negativeText("Cancel")
                .neutralText("Don't save")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        saveTemplate();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        backToMain();
                    }
                })
                .show();
    }

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

    // TODO: Copy this example when making dialogs into material style.
	private void newBehaviour() {
        Context context = this;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("New Behaviour")
                .autoDismiss(false)
                .customView(R.layout.dialog_behaviour, true)
                .negativeText("Cancel")
                .positiveText("Ok")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        if (view == null) {
                            return;
                        }

                        EditText bName = (EditText) view.findViewById(R.id.dialog_behaviour_name);
                        Spinner spinner = (Spinner) view.findViewById(R.id.behaviour_type_spinner);
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_behaviour_separate);

                        String name = bName.getText().toString().trim();
                        if (!"".equals(name) && !name.contains(",") && !name.contains(";") && !name.contains(":")) {
                            Behaviour b = new Behaviour();
                            b.setName(name);
                            b.setType(spinner.getSelectedItemPosition());
                            b.setSeparate(checkBox.isChecked());
                            newTemp.behaviours.add(b);
                            adapter.refresh();

                            dialog.dismiss();
                        } else {
                            makeSomeToast("Invalid behaviour name");
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build();

        View view = dialog.getCustomView();
        if (view == null) {
            return;
        }

        final Spinner spinner = (Spinner) view.findViewById(R.id.behaviour_type_spinner);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_behaviour_separate);

        String[] spinnerArray = new String[]{"State","Event"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == BehaviourType.STATE) {
                    checkBox.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        dialog.show();
	}
	
	// Brings up the dialog box with added remove button as well as info about behaviour.
	public void editBehaviour(final Context context, final int index) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Edit Behaviour")
                .autoDismiss(false)
                .customView(R.layout.dialog_behaviour, true)
                .negativeText("Cancel")
                .neutralText("Remove")
                .positiveText("Ok")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        if (view == null) {
                            return;
                        }

                        final EditText bName = (EditText) view.findViewById(R.id.dialog_behaviour_name);
                        final Spinner spinner = (Spinner) view.findViewById(R.id.behaviour_type_spinner);
                        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_behaviour_separate);

                        String name = bName.getText().toString().trim();
                        if (!"".equals(name) && !name.contains(",") && !name.contains(";") && !name.contains(":")) {
                            newTemp.behaviours.get(index).setName(name);
                            newTemp.behaviours.get(index).setType(spinner.getSelectedItemPosition());
                            newTemp.behaviours.get(index).setSeparate(checkBox.isChecked());
                            adapter.refresh();

                            dialog.dismiss();
                        } else {
                            makeSomeToast("Invalid behaviour name");
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        newTemp.behaviours.remove(index);
                        adapter.refresh();
                        dialog.dismiss();
                    }
                })
                .build();

        View view = dialog.getCustomView();
        if (view == null) {
            return;
        }

        final EditText bName = (EditText) view.findViewById(R.id.dialog_behaviour_name);
        final Spinner spinner = (Spinner) view.findViewById(R.id.behaviour_type_spinner);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_behaviour_separate);

        String[] spinnerArray = new String[]{"State","Event"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == BehaviourType.STATE) {
                    checkBox.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // get the current behaviour
        Behaviour currentBehaviour = newTemp.behaviours.get(index);
		bName.setText(currentBehaviour.getName());
        spinner.setSelection(currentBehaviour.getType());

        if (currentBehaviour.getType() == BehaviourType.EVENT) {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setChecked(currentBehaviour.isSeparate());
        }

        dialog.show();
	}

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
	