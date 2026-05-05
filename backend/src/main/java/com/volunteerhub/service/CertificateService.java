package com.volunteerhub.service;

import com.volunteerhub.model.User;
import com.volunteerhub.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CertificateService {
    private final UserRepository users;

    public CertificateService(UserRepository users) {
        this.users = users;
    }

    public byte[] certificateFor(String volunteerId) {
        User volunteer = users.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer not found"));
        String title = "Certificate of Volunteer Service";
        String body = "This certifies that " + volunteer.getName()
                + " contributed " + volunteer.getTotalHours()
                + " verified volunteer hours through VolunteerHub.";
        String date = "Issued on " + LocalDate.now();
        return simplePdf(title, body, date);
    }

    private byte[] simplePdf(String title, String body, String date) {
        String escapedTitle = escape(title);
        String escapedBody = escape(body);
        String escapedDate = escape(date);
        String stream = "BT /F1 26 Tf 72 700 Td (" + escapedTitle + ") Tj "
                + "/F1 16 Tf 0 -70 Td (" + escapedBody + ") Tj "
                + "/F1 14 Tf 0 -40 Td (" + escapedDate + ") Tj ET";
        String[] objects = {
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + stream.getBytes(StandardCharsets.US_ASCII).length + " >> stream\n" + stream + "\nendstream"
        };

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        int[] offsets = new int[objects.length + 1];
        for (int i = 0; i < objects.length; i++) {
            offsets[i + 1] = pdf.toString().getBytes(StandardCharsets.US_ASCII).length;
            pdf.append(i + 1).append(" 0 obj ").append(objects[i]).append(" endobj\n");
        }
        int xrefOffset = pdf.toString().getBytes(StandardCharsets.US_ASCII).length;
        pdf.append("xref\n0 ").append(objects.length + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (int i = 1; i < offsets.length; i++) {
            pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        }
        pdf.append("trailer << /Root 1 0 R /Size ").append(objects.length + 1).append(" >>\n");
        pdf.append("startxref\n").append(xrefOffset).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
