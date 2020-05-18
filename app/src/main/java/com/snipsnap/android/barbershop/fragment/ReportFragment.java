package com.snipsnap.android.barbershop.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.snipsnap.android.barbershop.BuildConfig;
import com.snipsnap.android.barbershop.R;
import com.snipsnap.android.barbershop.databinding.FragmentReportBinding;
import com.snipsnap.android.barbershop.helpers.AppointmentModel;
import com.snipsnap.android.barbershop.helpers.BarberViewModel;
import com.snipsnap.android.barbershop.helpers.CalendarAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private FragmentReportBinding mReportBinding;
    private BarberViewModel mBarberViewModel;
    private MaterialTextView mTxtv_greeting;
    private Spinner mSpinnerStart;
    private Spinner mSpinnerEnd;
    private String monthStart;
    private String monthEnd;
    private Button btn_generateReport;
    final private String TAG = "barber: RF";
    private File pdfFile;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private List<AppointmentModel> reportList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBarberViewModel = new ViewModelProvider(requireActivity())
                .get(BarberViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mReportBinding = FragmentReportBinding.inflate(inflater,
                container, false);
        return mReportBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTxtv_greeting = mReportBinding.TXTVGreeting;
        btn_generateReport = mReportBinding.BTNMonthReport;
        mSpinnerStart = mReportBinding.spinnerMonthsStart;
        mSpinnerEnd = mReportBinding.spinnerMonthsEnd;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(requireContext(),
                        R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerStart.setAdapter(adapter);
        mSpinnerStart.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
                .createFromResource(requireContext(),
                        R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerEnd.setAdapter(adapter2);
        mSpinnerEnd.setOnItemSelectedListener(new classMonthEnd());
    }

    @Override
    public void onResume() {

        btn_generateReport.setOnClickListener(d -> {
            mBarberViewModel.getAppointmentByMonth(monthStart, monthEnd).observe(getViewLifecycleOwner(), am -> {
                reportList = am;
            });
            try {
                createPDFWrapper();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        });
        super.onResume();
    }

    private void createPDFWrapper() throws IOException, DocumentException {
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

    private void createPdf() throws IOException, DocumentException {
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
        PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 3});

        // Handle Table
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Client Name");
//        table.addCell("Type");
        table.addCell("Service");
        table.addCell("Price");
        table.addCell("Date");
//        table.addCell("Date");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.CYAN);
        }
        //for loop to insert data
        table.getDefaultCell().setBorder(0);
        int i = 1;
        Double total = 0.0d;
        for (AppointmentModel am : reportList) {
            table.addCell(am.cFirstName);
            table.addCell(am.serviceName);
            table.addCell(am.price.toString());
            table.addCell(am.apptDate);
            total += am.price.floatValue();
            i++;
        }

        int j = 0;
        for (int k = 1; k < i; k++) {
            PdfPCell[] infocells = table.getRow(k).getCells();
            if (j % 2 == 0) {
                for (PdfPCell cell : infocells) {
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                }
            }
            j++;
        }
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.snipsnap_logo_text);
        PdfWriter.getInstance(document, output);
        document.open();
//        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE);
        Font n = new Font(Font.FontFamily.TIMES_ROMAN, 50.0f, Font.NORMAL);
        String sname = reportList.get(0).shopName;
        Paragraph shopName = new Paragraph(sname, n);
        shopName.setAlignment(Element.ALIGN_CENTER);
        document.add(shopName);

        Font a = new Font(Font.FontFamily.TIMES_ROMAN, 40.0f, Font.NORMAL);
        a.setColor(122, 119, 119);
        String saddr = reportList.get(0).shopAddr;
        Paragraph shopAddr = new Paragraph(saddr, a);
        shopAddr.setAlignment(Element.ALIGN_CENTER);
        document.add(shopAddr);

        Font d = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.NORMAL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String reportDate = "Date: ";
        String date = sdf.format(new Date());
        reportDate = reportDate.concat(date);
        Paragraph reportdate = new Paragraph(reportDate, d);
        reportdate.setAlignment(Element.ALIGN_CENTER);
        document.add(reportdate);


        Font v = new Font(Font.FontFamily.TIMES_ROMAN, 40.0f, Font.UNDERLINE);
        a.setColor(122, 119, 119);
        String monthReportlabel = "Report on months:";
        Paragraph monthReport = new Paragraph(monthReportlabel, v);
        monthReport.setAlignment(Element.ALIGN_CENTER);
        document.add(monthReport);

        Font df = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.NORMAL);
        String monthstart = "Start Month: "+ monthStart;
        Paragraph reportsmonth = new Paragraph(monthstart, df);
        reportsmonth.setAlignment(Element.ALIGN_CENTER);
        document.add(reportsmonth);

        Font de = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.NORMAL);
        String monthend = "End Month: "+ monthEnd;
        Paragraph reportsmonthend = new Paragraph(monthend, de);
        reportsmonthend.setAlignment(Element.ALIGN_CENTER);
        document.add(reportsmonthend);


        Font bd = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.NORMAL);
        String bdesc = "Barber:";
        Paragraph barberDesc = new Paragraph(bdesc, bd);
        barberDesc.setAlignment(Element.ALIGN_LEFT);
        document.add(barberDesc);

        Font b = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL);
        String bname = reportList.get(0).bFirstName;
        bname = bname.concat(" " + reportList.get(0).bLastName);
        Paragraph barberName = new Paragraph(bname, b);
        barberName.setAlignment(Element.ALIGN_LEFT);
        document.add(barberName);

        Paragraph newLine = new Paragraph("\n");
        document.add(newLine);

        // Adding an image
//        Image img = Image.getInstance(bmp):
        document.add(table);

        Double newTotal = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Font t = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.UNDERLINE);
        String totallabel = "Month(s) Total: " + newTotal.toString();
        Paragraph totalLabel= new Paragraph(totallabel, t);
        totalLabel.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalLabel);
        document.add(newLine);

        Double newAvg = total/i;
        newAvg = BigDecimal.valueOf(newAvg).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Font ta = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.UNDERLINE);
        String avglabel = "Average: " + newAvg.toString();
        Paragraph avgLabel= new Paragraph(avglabel, ta);
        avgLabel.setAlignment(Element.ALIGN_RIGHT);
        document.add(avgLabel);
        document.add(newLine);

        try {
            Drawable drawable = requireContext().getDrawable(R.drawable.snipsnap_logo_text_256px);
            BitmapDrawable bitDw = ((BitmapDrawable) drawable);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            intent.setDataAndType(reportURI, "application/pdf");
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
        mReportBinding = null;
        super.onDestroyView();
    }

    public void getMonthsReport (){

    }
    public class classMonthEnd implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            String month = parent.getItemAtPosition(pos).toString();
            String newMonth = "2020-";
            newMonth= newMonth.concat(parseMonthString(month)+"-01");
            monthEnd = newMonth;
            Log.d(TAG, monthEnd);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String month = parent.getItemAtPosition(position).toString();
        String newMonth = "2020-";
        newMonth= newMonth.concat(parseMonthString(month)+"-01");
        monthStart = newMonth;
        Log.d(TAG, monthStart);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String parseMonthString(String month) {
        switch(month) {
            case "January":
                return "01";
            case "February":
                return "02";
            case "March":
                return "03";
            case "April":
                return "04";
            case "May":
                return "05";
            case "June":
                return "06";
            case "July":
                return "07";
            case "August":
                return "08";
            case "September":
                return "09";
            case "October":
                return "10";
            case "November":
                return "11";
            case "December":
                return "12";
        }
        return "month";
    }
}
