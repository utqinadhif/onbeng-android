package com.nadhif.onbeng;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nadhif on 21/01/2016.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;

    private static ArrayList<DataRecycler> dataRecycler;

    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    static SharedPreferences sp;
    static ContentValues cv;
    static Context context;

    public MyAdapter(ArrayList<DataRecycler> dataRecycler, RecyclerView recyclerView, Context context) {
        this.dataRecycler = dataRecycler;
        sp = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        this.context = context;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                                 @Override
                                                 public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                     super.onScrolled(recyclerView, dx, dy);

                                                     totalItemCount = linearLayoutManager.getItemCount();
                                                     lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                                                     if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                                         if (onLoadMoreListener != null) {
                                                             onLoadMoreListener.onLoadMore();
                                                         }
                                                         loading = true;
                                                     }
                                                 }
                                             }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order, parent, false);
        vh = new ListOrderViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListOrderViewHolder) {
            DataRecycler listorder = dataRecycler.get(position);

            ((ListOrderViewHolder) holder).logoBengkel.setText(listorder.getLogoBengkel());
            ((ListOrderViewHolder) holder).nameBengkel.setText(listorder.getNameBengkel());
            ((ListOrderViewHolder) holder).dateOrder.setText(listorder.getDateOrder());
//            'waiting', 'process', 'confirm', 'cancel', 'done'
            if (listorder.getStatusOrder().equals("0")) {
                ((ListOrderViewHolder) holder).statusOrder.setImageResource(R.drawable.waiting);
            } else if (listorder.getStatusOrder().equals("1")) {
                ((ListOrderViewHolder) holder).statusOrder.setImageResource(R.drawable.process);
            } else if (listorder.getStatusOrder().equals("2")) {
                ((ListOrderViewHolder) holder).statusOrder.setImageResource(R.drawable.confirm);
            } else if (listorder.getStatusOrder().equals("3")) {
                ((ListOrderViewHolder) holder).statusOrder.setImageResource(R.drawable.cancel);
            } else if (listorder.getStatusOrder().equals("4")) {
                ((ListOrderViewHolder) holder).statusOrder.setImageResource(R.drawable.done);
            }
            ((ListOrderViewHolder) holder).damageOrder.setText(listorder.getDamageOrder());
            ((ListOrderViewHolder) holder).numberOrder.setText(String.valueOf(position + 1));

            ((ListOrderViewHolder) holder).list_order = listorder;
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return dataRecycler != null ? dataRecycler.size() : 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public static class ListOrderViewHolder extends RecyclerView.ViewHolder {
        public TextView logoBengkel, nameBengkel, dateOrder, damageOrder, numberOrder;
        public ImageView statusOrder;
        public DataRecycler list_order;
        Dialog dialog;
        RadioButton confirm, cancel;
        Button change_status, dismiss_status;

        public ListOrderViewHolder(final View v) {
            super(v);
            logoBengkel = (TextView) v.findViewById(R.id.logoBengkel);
            nameBengkel = (TextView) v.findViewById(R.id.nameBengkel);
            dateOrder = (TextView) v.findViewById(R.id.dateOrder);
            statusOrder = (ImageView) v.findViewById(R.id.statusOrder);
            damageOrder = (TextView) v.findViewById(R.id.damageOrder);
            numberOrder = (TextView) v.findViewById(R.id.numberOrder);

            String restoredText = sp.getString("login", null);
            cv = new ContentValues();
            if (restoredText != null) {
                cv.put("username", sp.getString("username", null));
                cv.put("password", sp.getString("passwordh", null));
            }

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View vi) {
                    if (list_order.getStatusOrder().equals("0")) {
                        new AlertDialog.Builder(v.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Confirmation")
                                .setMessage(R.string.confirm_change_status)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new ChangeStatus(context, Config.url + "form/change_status/3/" + list_order.getId(), cv).execute();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                    } else if (list_order.getStatusOrder().equals("1")) {
                        dialog = new Dialog(v.getContext());
                        dialog.setTitle("Change Your Order Status");
                        dialog.setContentView(R.layout.pop_two_option);

                        confirm = (RadioButton) dialog.findViewById(R.id.change_confirm);
                        cancel = (RadioButton) dialog.findViewById(R.id.change_cancel);

                        change_status = (Button) dialog.findViewById(R.id.change_status);
                        dismiss_status = (Button) dialog.findViewById(R.id.dismiss_status);

                        change_status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (confirm.isChecked()) {
                                    new ChangeStatus(context, Config.url + "form/change_status/2/" + list_order.getId(), cv).execute();
                                } else if (cancel.isChecked()) {
                                    new ChangeStatus(context, Config.url + "form/change_status/3/" + list_order.getId(), cv).execute();
                                } else {
                                    Toast.makeText(v.getContext(), "No option selected.", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        dismiss_status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (list_order.getStatusOrder().equals("2") || list_order.getStatusOrder().equals("3") || list_order.getStatusOrder().equals("4")) {
                        Toast.makeText(v.getContext(), "In this status, your can't change the ststus.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailOrderActivity.class);
                    intent.putExtra("detail bengkel", list_order.getDetail_bengkel());
                    intent.putExtra("detail order", list_order.getDetail_order());
                    intent.putExtra("status order", list_order.getStatusOrder());
                    intent.putExtra("status order text", list_order.getStatusOrderText());
                    intent.putExtra("id", list_order.getDateOrder() + " " + list_order.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }

        private class ChangeStatus extends Curl {
            public ChangeStatus(Context context, String url, ContentValues cv) {
                super(context, url, cv);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.getString("ok").equals("1")) {
                        JSONObject c = json.getJSONObject("result");
                        int[] im = {R.drawable.waiting, R.drawable.process, R.drawable.confirm, R.drawable.cancel, R.drawable.done};
                        statusOrder.setImageResource(im[Integer.parseInt(c.getString("status"))]);
                        int g = getAdapterPosition();
                        dataRecycler.get(g).setStatusOrder(c.getString("status"));
                        dataRecycler.get(g).setStatusOrderText(c.getString("status_text"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
