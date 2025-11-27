package com.example.admincollegeapp.notice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfDataViewHolder> {
    private Context context;
    private ArrayList<PdfData> pdfDataList;
    private PdfItemClickListener listener;

    public PdfAdapter(Context context, ArrayList<PdfData> pdfDataList, PdfItemClickListener listener) {
        this.context = context;
        this.pdfDataList = pdfDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PdfDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_item_delete_layout, parent, false);
        return new PdfDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfDataViewHolder holder, int position) {
        PdfData currentItem = pdfDataList.get(position);

        holder.pdfTitleTextView.setText(currentItem.getPdfTitle());

        // Open PDF on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(currentItem.getPdfUrl()), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(Intent.createChooser(intent, "Open PDF"));
            } catch (Exception e) {
                Toast.makeText(context, "No PDF Viewer Found", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this PDF?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (listener != null) {
                            listener.onDeleteClick(position);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return pdfDataList.size();
    }

    public static class PdfDataViewHolder extends RecyclerView.ViewHolder {
        private TextView pdfTitleTextView;
        private MaterialButton deleteButton;

        public PdfDataViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfTitleTextView = itemView.findViewById(R.id.pdfTitleTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface PdfItemClickListener {
        void onDeleteClick(int position);
    }
}