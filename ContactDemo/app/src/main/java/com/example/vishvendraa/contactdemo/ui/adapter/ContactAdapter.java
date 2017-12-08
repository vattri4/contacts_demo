package com.example.vishvendraa.contactdemo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vishvendraa.contactdemo.utils.ItemClick;
import com.example.vishvendraa.contactdemo.R;
import com.example.vishvendraa.contactdemo.model.Contact;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vishvendraa on 06-12-2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> mContactList;
    private ItemClick mItemClick;

    public ContactAdapter(List<Contact> list, ItemClick itemClick) {
        this.mItemClick = itemClick;
        this.mContactList = list;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        holder.name.setText(mContactList.get(position).getName());
        holder.phoneNumber.setText(mContactList.get(position).getPhone());
        final Contact contact = mContactList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClick.onClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phoneNumber = itemView.findViewById(R.id.phone_number);
        }
    }

    public void setFilter(List<Contact> list, boolean flag) {
        mContactList.clear();
        mContactList.addAll(list);
        if (flag) {
            Collections.sort(mContactList, new Comparator<Contact>() {
                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return rhs.getName().compareTo(lhs.getName());
                }
            });
        } else {
            Collections.sort(mContactList, new Comparator<Contact>() {
                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
        notifyDataSetChanged();
    }
}
