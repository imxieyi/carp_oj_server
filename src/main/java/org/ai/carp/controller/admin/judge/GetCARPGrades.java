package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.judge.QueryTopResult;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/admin/judge/grades/carpall", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
public class GetCARPGrades {

    private class IntWrapper {
        public int num = 0;
    }

    @GetMapping
    public ResponseEntity<byte[]> get(HttpSession session) throws IOException {
        UserUtils.getUser(session, User.ROOT);
        Workbook wb = new XSSFWorkbook();
        Sheet finalSheet = wb.createSheet("Final");
        Row finalTitle = finalSheet.createRow(0);
        finalTitle.createCell(0).setCellValue("ID");
        Map<String, Row> stuFinalMap = new HashMap<>();
        IntWrapper baseCol = new IntWrapper();
        baseCol.num = -2;
        Database.getInstance().getCarpDatasets().findAll()
                .stream().filter(CARPDataset::isFinalJudge).forEach(d -> {
                    // Add combined data
                    baseCol.num += 3;
                    finalTitle.createCell(baseCol.num).setCellValue(d.getName());
                    finalTitle.createCell(baseCol.num+1).setCellValue("Time");
                    finalTitle.createCell(baseCol.num+2).setCellValue("Count");
                    QueryTopResult.getFinalList(d.getId()).forEach(c -> {
                        Row r;
                        if (!stuFinalMap.containsKey(c.getUserName())) {
                            r = finalSheet.createRow(finalSheet.getLastRowNum()+1);
                            r.createCell(0).setCellValue(c.getUserName());
                            stuFinalMap.put(c.getUserName(), r);
                        } else {
                            r = stuFinalMap.get(c.getUserName());
                        }
                        r.createCell(baseCol.num).setCellValue(c.getResult());
                        r.createCell(baseCol.num+1).setCellValue(c.getTime());
                        r.createCell(baseCol.num+2).setCellValue(c.getCount());
                    });
                    finalSheet.autoSizeColumn(baseCol.num);
                    finalSheet.autoSizeColumn(baseCol.num+1);
                    finalSheet.autoSizeColumn(baseCol.num+2);
                    // Create dataset sheet
                    Sheet sheet = wb.createSheet(d.getName());
                    Row row = sheet.createRow(0);
                    row.createCell(0).setCellValue("ID");
                    for (int i=0; i<5; i++) {
                        row.createCell(i*3+1).setCellValue(String.valueOf(i+1));
                        row.createCell(i*3+2).setCellValue("Time");
                        row.createCell(i*3+3).setCellValue("Reason");
                    }
                    // Add dataset data
                    Map<String, Row> stuMap = new HashMap<>();
                    Database.getInstance().getCarpCases()
                            .findCARPCasesByDatasetOrderBySubmitTimeDesc(d)
                            .stream().forEach(c -> {
                        Row r;
                        if (!stuMap.containsKey(c.getUser().getUsername())) {
                            r = sheet.createRow(sheet.getLastRowNum()+1);
                            r.createCell(0).setCellValue(c.getUser().getUsername());
                            stuMap.put(c.getUser().getUsername(), r);
                        } else {
                            r = stuMap.get(c.getUser().getUsername());
                        }
                        r.createCell(r.getLastCellNum()).setCellValue(c.getCost());
                        r.createCell(r.getLastCellNum()).setCellValue(c.getTime());
                        r.createCell(r.getLastCellNum()).setCellValue(c.getReason());
                    });
                    for (int i=0; i<=15; i++) {
                        sheet.autoSizeColumn(i);
                    }
                });
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "carp.xlsx");
        headers.setContentLength(baos.size());
        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

}
