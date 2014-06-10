package ru.yandex.qatools.allure.report;


/**
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 29.05.14
 *         <p/>
 *         Exception which notify that Allure report can't be created
 * @see ru.yandex.qatools.allure.report.AllureReportBuilder
 */
public class AllureReportBuilderException extends Exception {

    public AllureReportBuilderException(String message) {
        super(message);
    }

    public AllureReportBuilderException(Exception e) {
        super(e);
    }
}
