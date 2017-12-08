package com.example.vishvendraa.contactdemo.ui;


import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vishvendraa.contactdemo.services.ApiClient;
import com.example.vishvendraa.contactdemo.services.ApiInterface;
import com.example.vishvendraa.contactdemo.ui.adapter.ContactAdapter;
import com.example.vishvendraa.contactdemo.utils.ItemClick;
import com.example.vishvendraa.contactdemo.R;
import com.example.vishvendraa.contactdemo.model.Contact;
import com.example.vishvendraa.contactdemo.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishvendraa on 07-12-2017.
 */

public class ContactFragment extends Fragment implements ItemClick, SearchView.OnQueryTextListener {
    private ApiInterface apiInterface;
    private ContactAdapter mAdapter;
    private List<Contact> mFilteredContactList;
    private boolean checkFlag = false;
    private RelativeLayout mProgress;
    private List<Contact> mContactList;
    private AlertDialog b;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContactList = new ArrayList<>();
        mFilteredContactList = new ArrayList<>();
        mAdapter = new ContactAdapter(mFilteredContactList, this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);
        if (b != null && b.isShowing()) {
            b.show();
        }
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerview);
        SwitchCompat mSwitch = view.findViewById(R.id.switch_button);
        mProgress = view.findViewById(R.id.progress_container);
        if (NetworkUtils.isNetworkAvailable(getContext())) {
            mProgress.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 3000);
        } else {
            Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
        }
        mRecyclerView.setHasFixedSize(true);
        setHasOptionsMenu(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkFlag = true;
                    Collections.sort(mFilteredContactList, new Comparator<Contact>() {
                        @Override
                        public int compare(Contact lhs, Contact rhs) {
                            return rhs.getName().compareTo(lhs.getName());

                        }
                    });
                } else {
                    checkFlag = false;
                    Collections.sort(mFilteredContactList, new Comparator<Contact>() {
                        @Override
                        public int compare(Contact lhs, Contact rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    private void loadData() {
        Call<List<Contact>> call = apiInterface.getContactList();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                mProgress.setVisibility(View.GONE);
                mContactList.addAll(response.body());
                mFilteredContactList.addAll(response.body());
                Collections.sort(mFilteredContactList, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact lhs, Contact rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(Contact contact) {
        showChangeLangDialog(contact);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Contact> filteredList = filter(mContactList, newText);
        mAdapter.setFilter(filteredList, checkFlag);
        return false;
    }

    private List<Contact> filter(List<Contact> contacts, String query) {
        query = query.toLowerCase();
        final List<Contact> contactList = new ArrayList<>();
        for (Contact model : contacts) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                contactList.add(model);
            }
        }
        return contactList;
    }

    public void showChangeLangDialog(Contact contact) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);
        TextView address = (TextView) dialogView.findViewById(R.id.address);
        TextView name = (TextView) dialogView.findViewById(R.id.name);
        TextView email = (TextView) dialogView.findViewById(R.id.email);
        TextView phone = (TextView) dialogView.findViewById(R.id.phone);
        TextView website = (TextView) dialogView.findViewById(R.id.website);
        TextView company = (TextView) dialogView.findViewById(R.id.company);
        TextView latlong = (TextView) dialogView.findViewById(R.id.latlong);
        address.setText(String.format("%s %s %s %s %s", getString(R.string.address), contact.getAddress().getSuite(), contact.getAddress().getStreet(), contact.getAddress().getCity(), contact.getAddress().getZipcode()));
        latlong.setText(String.format("%s %s ", contact.getAddress().getGeo().getLat(), contact.getAddress().getGeo().getLng()));
        name.setText(String.format("%s %s", getString(R.string.name), contact.getName()));
        email.setText(String.format("%s %s", getString(R.string.email), contact.getEmail()));
        phone.setText(String.format("%s %s", getString(R.string.phone), contact.getPhone()));
        website.setText(String.format("%s %s", getString(R.string.website), contact.getWebsite()));
        company.setText(String.format("%s %s %s %s", getString(R.string.company), contact.getCompany().getName(), contact.getCompany().getCatchPhrase(), contact.getCompany().getBs()));
        dialogBuilder.setTitle(R.string.contact_details);
        dialogBuilder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        b = dialogBuilder.create();
        b.show();
    }
}

