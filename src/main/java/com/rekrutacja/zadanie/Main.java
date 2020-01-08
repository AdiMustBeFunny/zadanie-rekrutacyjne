package com.rekrutacja.zadanie;

import java.awt.*;
import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        LocalDate date = getDateFromUser();

        Optional<BNPResponseDTO> responseDTO = WebScrapper.getDataFromBNP(date);

        if (responseDTO.isPresent() == false)
        {
            System.out.println("Try to pick a recent date or check your internet connection");
            return;
        }

        visualizeData(responseDTO.get());

    }

    public static LocalDate getDateFromUser() {
        Scanner input = new Scanner(System.in);

        boolean dateIsValid = false;
        LocalDate date = null;

        System.out.println("Check the bid and ask values of USD from a recent date until now");

        while (dateIsValid == false) {
            int year, month, day;

            System.out.println("Please provide a recent date");
            System.out.println("Year: ");
            year = input.nextInt();
            System.out.println("Month: ");
            month = input.nextInt();
            System.out.println("Day: ");
            day = input.nextInt();

            try {
                date = LocalDate.of(year, month, day);
                dateIsValid = true;
            } catch (DateTimeException dtex) {
                System.out.println("Please provide a valid date!");
            }
        }

        return date;
    }

    private static void visualizeData(BNPResponseDTO responseDTO)
    {

        Scanner input = new Scanner(System.in);


        List<Double> askChangeRate = new ArrayList() {{
            add(0.0);
        }};
        List<Double> bidChangeRate = new ArrayList() {{
            add(0.0);
        }};

        for (int i = 1; i < responseDTO.getRates().size(); i++) {
            askChangeRate.add(responseDTO.getRates().get(i).getAsk() -
                    responseDTO.getRates().get(i - 1).getAsk());
            bidChangeRate.add(responseDTO.getRates().get(i).getBid() -
                    responseDTO.getRates().get(i - 1).getBid());
        }

        for (int i = 0; i < responseDTO.getRates().size(); i++) {
            System.out.println(responseDTO.getRates().get(i).getEffectiveDate()
                    + ", Bid value: " + responseDTO.getRates().get(i).getBid() +
                    ", Ask value: " + responseDTO.getRates().get(i).getAsk() +
                    ", Bid change: " + bidChangeRate.get(i) +
                    ", Ask change: " + askChangeRate.get(i));
        }


        System.out.println("Would you like to view this data in your browser? (y/n)");
        String choice = "x";

        while ((choice.charAt(0) != 'y' && choice.charAt(0) != 'n') || choice.isEmpty())
            choice = input.nextLine();

        if (choice.charAt(0) == 'y') {
            viewDataInTheBrowser(responseDTO,askChangeRate,bidChangeRate);
        }
    }

    private static void viewDataInTheBrowser(BNPResponseDTO responseDTO,List<Double> askChangeRate,List<Double> bidChangeRate){
        File f = new File("src/html/boilerplate.html");
        if (f.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String htmlFile = builder.toString();

                StringBuilder askRatesStringBuilder = new StringBuilder();
                StringBuilder bidRatesStringBuilder = new StringBuilder();
                StringBuilder labelStringBuilder = new StringBuilder();

                for (int i = 0; i < responseDTO.getRates().size(); i++) {
                    if (i == askChangeRate.size() - 1)
                        askRatesStringBuilder.append(responseDTO.getRates().get(i).getAsk());
                    else
                        askRatesStringBuilder.append(responseDTO.getRates().get(i).getAsk() + ",");
                }
                for (int i = 0; i < responseDTO.getRates().size(); i++) {
                    if (i == askChangeRate.size() - 1)
                        bidRatesStringBuilder.append(responseDTO.getRates().get(i).getBid());
                    else
                        bidRatesStringBuilder.append(responseDTO.getRates().get(i).getBid() + ",");
                }
                for (int i = 0; i < responseDTO.getRates().size(); i++) {
                    if (i == askChangeRate.size() - 1)
                        labelStringBuilder.append("'" + responseDTO.getRates().get(i).getEffectiveDate() + "'");
                    else
                        labelStringBuilder.append("'" + responseDTO.getRates().get(i).getEffectiveDate() + "'" + ",");
                }

                htmlFile = htmlFile.replace("<ask-prices>", askRatesStringBuilder.toString());
                htmlFile = htmlFile.replace("<bid-prices>", bidRatesStringBuilder.toString());
                htmlFile = htmlFile.replace("<labels>", labelStringBuilder.toString());


                File outHtml = new File("src/html/out.html");
                BufferedWriter writer = new BufferedWriter(new FileWriter(outHtml));
                writer.write(htmlFile);
                writer.close();

                Desktop.getDesktop().browse(outHtml.toURI());
            }
            catch (IOException ioex)
            {
                System.out.println(ioex.getMessage());
            }
        }
    }

}
