package com.zizto;

import com.zizto.dao.BillDao;
import com.zizto.dao.ServiceDao;
import com.zizto.dao.SubscriberDao;
import com.zizto.model.Bill;
import com.zizto.model.Service;
import com.zizto.util.JpaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::shutdown));

        ServiceDao serviceDao = new ServiceDao();
        SubscriberDao subscriberDao = new SubscriberDao();
        BillDao billDao = new BillDao();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Меню Телефонной Станции (JPA) ---");
            System.out.println("1. Вывести список доступных услуг");
            System.out.println("2. Вывести информацию об услугах заданного абонента");
            System.out.println("3. Вывести неоплаченный счет заданного абонента");
            System.out.println("4. Оплатить счет");
            System.out.println("5. Заблокировать абонента");
            System.out.println("6. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.println("--- Доступные услуги ---");
                        List<Service> allServices = serviceDao.getAllServices();
                        allServices.forEach(System.out::println);
                        break;
                    case 2:
                        System.out.print("Введите ID абонента: ");
                        int subIdForServices = Integer.parseInt(scanner.nextLine());
                        System.out.println("--- Услуги абонента ID " + subIdForServices + " ---");
                        List<Service> subServices = serviceDao.getServicesBySubscriberId(subIdForServices);
                        if (subServices.isEmpty()) {
                            System.out.println("У абонента нет подключенных услуг.");
                        } else {
                            subServices.forEach(System.out::println);
                        }
                        break;
                    case 3:
                        System.out.print("Введите ID абонента: ");
                        int subIdForBill = Integer.parseInt(scanner.nextLine());
                        System.out.println("--- Неоплаченный счет абонента ID " + subIdForBill + " ---");
                        Bill bill = billDao.getUnpaidBillBySubscriberId(subIdForBill);
                        if (bill != null) {
                            System.out.println(bill);
                        } else {
                            System.out.println("Неоплаченных счетов для данного абонента не найдено.");
                        }
                        break;
                    case 4:
                        System.out.print("Введите ID счета для оплаты: ");
                        int billIdToPay = Integer.parseInt(scanner.nextLine());
                        billDao.payBill(billIdToPay);
                        System.out.println("Счет с ID " + billIdToPay + " успешно оплачен.");
                        break;
                    case 5:
                        System.out.print("Введите ID абонента для блокировки: ");
                        int subIdToBlock = Integer.parseInt(scanner.nextLine());
                        subscriberDao.blockSubscriber(subIdToBlock);
                        System.out.println("Абонент с ID " + subIdToBlock + " заблокирован.");
                        break;
                    case 6:
                        System.out.println("Программа завершена.");
                        scanner.close();
                        JpaUtil.shutdown();
                        return;
                    default:
                        System.out.println("Неверный ввод. Пожалуйста, выберите пункт из меню.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введенное значение не является корректным ID.");
            } catch (Exception e) {
                LOGGER.error("Произошла непредвиденная ошибка при выполнении операции '{}'", choice, e);
                System.out.println("Произошла ошибка. Подробности в логах.");
            }
        }
    }
}


