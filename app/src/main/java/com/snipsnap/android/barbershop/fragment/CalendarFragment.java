package com.snipsnap.android.barbershop.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.snipsnap.android.barbershop.BuildConfig;
import com.snipsnap.android.barbershop.GetApptByUsernameQuery;
import com.snipsnap.android.barbershop.databinding.FragmentCalendarBinding;
import com.snipsnap.android.barbershop.helpers.AppointmentModel;
import com.snipsnap.android.barbershop.helpers.BarberViewModel;
import com.snipsnap.android.barbershop.helpers.CalendarAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;


public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding mCalendarBinding;
    private CalendarView mCalendar;
    private BarberViewModel mBarberViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mTxtv_barberName;
    final private String TAG = "barbershop: calV";

    private final int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    private Button btn;
    private List<AppointmentModel> reportList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBarberViewModel = new ViewModelProvider(requireActivity())
                .get(BarberViewModel.class);
        mBarberViewModel.loadBarberAppointments();

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mCalendarBinding = FragmentCalendarBinding.inflate(inflater,
                container, false);
        return mCalendarBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCalendar = mCalendarBinding.calendarView;
        mTxtv_barberName = mCalendarBinding.TXTVBarber;
        mRecyclerView = mCalendarBinding.barberRecyclerView;

        btn = mCalendarBinding.button2;

        // recyclerView.setHasFixedSize(true)
        // Can layout manager and adapter be called in onCreatew?
        mLayoutManager = new LinearLayoutManager(requireActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBarberViewModel.getAllAppointments().observe(getViewLifecycleOwner(), am -> {
            if (am.isEmpty()) {
                Log.d(TAG, "No barber.");
                return;
            }
            String barberGreeting;
            barberGreeting = am.get(0).bFirstName;
            barberGreeting = barberGreeting.concat(" " + am.get(0).bLastName);
            mTxtv_barberName.setText(barberGreeting);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCalendar.setOnDateChangeListener((cv, year, month, day) -> {
            month = month + 1;
            String date = year + "-" + month + "-" + day;
//            Toast toast = Toast.makeText(getContext(), date, Toast.LENGTH_SHORT);
//            toast.show();
            mBarberViewModel.getAppointmentByDate(date).observe(getViewLifecycleOwner(), am -> {
                mAdapter = new CalendarAdapter(am);
                reportList = am;
                mRecyclerView.setAdapter(mAdapter);
            });
        });

        btn.setOnClickListener(r -> {
            try {
                createPDFWrapper();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
        });
    }

    private void createPDFWrapper() throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            createPdf();
        }
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        if (reportList.isEmpty()) {
            Log.d(TAG, "reportList is empty");
            return;
        }


        File filepath = requireContext().getFilesDir();
        File docsFolder = new File(filepath + "/Barber");
        if (!docsFolder.exists()) {
             if (docsFolder.mkdir()) {
                 Log.d(TAG, "Created a new directory for PDF");
             } else {
                 Log.d(TAG, "Did not create a new directory for PDF");
             }
        }

        String pdfname = "apptReport.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{3, 3, 3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Client Name");
//        table.addCell("Price");
//        table.addCell("Type");
        table.addCell("Service");
        table.addCell("Date");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        //for loop to insert data
        for (AppointmentModel am : reportList) {
            table.addCell(am.cFirstName);
            table.addCell(am.serviceName);
            table.addCell(am.apptDate);
        }

        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE);
        Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
        document.add(new Paragraph("Pdf Data \n\n", f));
        document.add(new Paragraph("Pdf File Through Itext", g));
        document.add(table);

        document.close();
        previewPdf();
    }

    private void previewPdf() {
        PackageManager packageManager = requireContext().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_ALL);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);


            Uri reportURI = FileProvider.getUriForFile(requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    pdfFile);
//            Uri uri = Uri.fromFile(pdfFile);
//            intent.setDataAndType(uri, "application/pdf");
            intent.setDataAndType(reportURI,"application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            requireActivity().startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageOKCancel(String s, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(requireContext())
                .setMessage(s)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onDestroyView() {
        mCalendarBinding = null;
        super.onDestroyView();
    }
}
