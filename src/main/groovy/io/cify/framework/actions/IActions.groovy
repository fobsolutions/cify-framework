package io.cify.framework.actions

import org.openqa.selenium.WebElement

/**
 * Actions interface
 *
 * Interface with basic actions user can perform
 */
public interface IActions {

    void click(WebElement element)

    void tap(WebElement element)

    void navigateBack()

    void fillIn(WebElement element, String text)

}
