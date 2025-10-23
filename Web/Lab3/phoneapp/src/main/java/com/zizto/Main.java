package com.zizto;

import com.zizto.dao.*;
import com.zizto.exception.DAOException;
import com.zizto.model.*;
import com.zizto.util.ConnectionPool;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ServiceDao serviceDao = new ServiceDao();
        SubscriberDao subscriberDao = new SubscriberDao();
        BillDao billDao = new BillDao();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Меню Телефонной Станции ---");
            System.out.println("1. Вывести список доступных услуг");
            System.out.println("2. Вывести информацию об услугах заданного абонента");
            System.out.println("3. Вывести неоплаченный счет заданного абонента");
            System.out.println("4. Оплатить счет");
            System.out.println("5. Заблокировать абонента");
            System.out.println("6. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                try {
                    System.out.println("--- Доступные услуги ---");
                    List<Service> allServices = serviceDao.getAllServices();
                    allServices.forEach(System.out::println);
                }
                catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }
                    break;
                case 2:
                try{
                    System.out.print("Введите ID абонента: ");
                    int subIdForServices = scanner.nextInt();
                    System.out.println("--- Услуги абонента ID " + subIdForServices + " ---");
                    List<Service> subServices = serviceDao.getServicesBySubscriberId(subIdForServices);
                    if (subServices.isEmpty()) {
                        System.out.println("У абонента нет подключенных услуг.");
                    } else {
                        subServices.forEach(System.out::println);
                    }
                }
                    catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }
                    break;
                case 3:
                try {
                    System.out.print("Введите ID абонента: ");
                    int subIdForBill = scanner.nextInt();
                    System.out.println("--- Неоплаченный счет абонента ID " + subIdForBill + " ---");
                    List<Bill> bills = billDao.getUnpaidBillBySubscriberId(subIdForBill);
                    if (!bills.isEmpty()) {
                        bills.forEach(System.out::println);
                    } else {
                        System.out.println("Неоплаченных счетов для данного абонента не найдено.");
                    }}
                    catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }

                    break;
                case 4:
                try {
                    System.out.print("Введите ID счета для оплаты: ");
                    int billIdToPay = scanner.nextInt();
                    billDao.payBill(billIdToPay);
                    System.out.println("Счет с ID " + billIdToPay + " успешно оплачен.");}
                    catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }

                    break;
                case 5:
                try {
                    System.out.print("Введите ID абонента для блокировки: ");
                    int subIdToBlock = scanner.nextInt();
                    subscriberDao.blockSubscriber(subIdToBlock);
                    System.out.println("Абонент с ID " + subIdToBlock + " заблокирован.");}
                    catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }

                    break;
                case 6:
                try {
                    System.out.println("Программа завершена.");
                    ConnectionPool.getInstance().shutdown();
                    scanner.close();}
                    catch (DAOException e) {
                    LOGGER.error("case 1 exception",e);
                }

                    return;
                default:
                    System.out.println("Неверный ввод. Пожалуйста, выберите пункт из меню.");
            }
        }
    }
}
