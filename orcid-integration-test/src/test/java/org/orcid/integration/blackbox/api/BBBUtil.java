/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.helper.SystemPropertiesHelper;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BBBUtil {

    public static void logUserOut(String baseUrl, WebDriver webDriver) {
        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
    }

    public static void revokeApplicationsAccess(WebDriver webDriver) {
        List<String> clientIds = new ArrayList<String>();
        Properties prop = SystemPropertiesHelper.getProperties();
        String clientId1 = prop.getProperty("org.orcid.web.testClient1.clientId");
        if (!PojoUtil.isEmpty(clientId1)) {
            clientIds.add(clientId1);
        }

        String clientId2 = prop.getProperty("org.orcid.web.testClient2.clientId");
        if (!PojoUtil.isEmpty(clientId2)) {
            clientIds.add(clientId2);
        }

        String userName = prop.getProperty("org.orcid.web.testUser1.username");
        String password = prop.getProperty("org.orcid.web.testUser1.password");
        String baseUrl = "https://localhost:8443/orcid-web";
        if (!PojoUtil.isEmpty(prop.getProperty("org.orcid.web.baseUri"))) {
            baseUrl = prop.getProperty("org.orcid.web.baseUri");
        }

        webDriver.get(baseUrl + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(baseUrl + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());

        SigninTest.signIn(webDriver, userName, password);

        // Switch to accounts settings page
        By accountSettingsMenuLink = By.id("accountSettingMenuLink");
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(accountSettingsMenuLink), webDriver);
        webDriver.get(baseUrl + "/account");
        BBBUtil.extremeWaitFor(BBBUtil.documentReady(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);

        try {
            boolean lookAgain = false;
            do {
                // Look for each revoke app button
                By revokeAppBtn = By.id("revokeAppBtn");
                BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                List<WebElement> appsToRevoke = webDriver.findElements(revokeAppBtn);
                boolean elementFound = false;
                // Iterate on them and delete the ones created by the specified
                // client id
                for (WebElement appElement : appsToRevoke) {
                    String nameAttribute = appElement.getAttribute("name");
                    if (clientIds.contains(nameAttribute)) {
                        BBBUtil.ngAwareClick(appElement, webDriver);
                        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
                        // Wait for the revoke button
                        By confirmRevokeAppBtn = By.id("confirmRevokeAppBtn");
                        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(confirmRevokeAppBtn), webDriver);
                        BBBUtil.ngAwareClick(webDriver.findElement(confirmRevokeAppBtn), webDriver);
                        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                        BBBUtil.noCboxOverlay(webDriver);
                        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
                        // may need to put sleep back here
                        elementFound = true;
                        break;
                    }
                }
                if (elementFound) {
                    lookAgain = true;
                } else {
                    lookAgain = false;
                }
            } while (lookAgain);
        } catch (Exception e) {
            // If it fail is because it couldn't find any other application
        } finally {
            logUserOut(baseUrl, webDriver);
        }
    }

    public static void ngAwareClick(WebElement webElement, WebDriver webDriver) {
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement).perform();
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        actions.click(webElement).perform();
    }

    public static void ngAwareSendKeys(String keys, String id, WebDriver webDriver) {
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        ((JavascriptExecutor) webDriver).executeScript(
                "" + "angular.element('#" + id + "').triggerHandler('focus');" + "angular.element('#" + id + "').val('" + keys + "');" + "angular.element('#" + id
                        + "').triggerHandler('change');" + "angular.element('#" + id + "').triggerHandler('blur');" + "angular.element('#" + id + "').scope().$apply();");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
    }

    public static void noSpinners(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("i.glyphicon-refresh")));
    }

    public static void noCboxOverlay(WebDriver webDriver) {
        (new WebDriverWait(webDriver, 20, 100)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='cboxOverlay']")));
    }

    public static void extremeWaitFor(ExpectedCondition<?> expectedCondition, WebDriver webDriver) {
        int wait = 20;
        int pollingInternval = 250;
        try {
            (new WebDriverWait(webDriver, wait, pollingInternval)).until(expectedCondition);
        } catch (Exception e) {
            ((JavascriptExecutor) webDriver).executeScript("$(window).trigger('resize');");
            (new WebDriverWait(webDriver, wait, pollingInternval)).until(expectedCondition);
        }
    }

    public static ExpectedCondition<Boolean> documentReady() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            }
        };
    }

    public static ExpectedCondition<Boolean> angularHasFinishedProcessing() {
        /*
         * Getting complex. 1. We want to make sure Angular is done. So you call
         * the rootScope apply 2. We want to make sure the browser is done
         * rendering the DOM so we call $timeout
         * http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-
         * finished-rendering/ 3. make sure there are no pending AJAX request,
         * if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                ((JavascriptExecutor) driver).executeScript("" + "window._selenium_angular_done = false;" + "function _seleniumAngularDone() { "
                        + "   angular.element(document.documentElement).scope().$root.$apply(" + "      function(){" + "        setTimeout(function(){ "
                        + "            if ($.active > 0)" + "               _seleniumAngularDone();" + "            else"
                        + "               window._selenium_angular_done = true;" + "         }, 0);" + "   });" + "};"
                        + "try { _seleniumAngularDone(); } catch(err) { /* do nothing */ }");
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("" + "return window._selenium_angular_done;").toString());
            }
        };
    }

    public static ExpectedCondition<Boolean> cboxComplete() {
        /*
         * Getting complex. 1. We want to make sure Angular is done. So you call
         * the rootScope apply 2. We want to make sure the browser is done
         * rendering the DOM so we call $timeout
         * http://blog.brunoscopelliti.com/run-a-directive-after-the-dom-has-
         * finished-rendering/ 3. make sure there are no pending AJAX request,
         * if so start over
         */
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("" + "return window.cbox_complete").toString());
            }
        };
    }

    public static final int TIMEOUT_SECONDS = 10;
    public static final int SLEEP_MILLISECONDS = 100;

}