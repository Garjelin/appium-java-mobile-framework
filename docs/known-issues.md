# Known issues

App defects found by the tests. A test blocked by a defect is **skipped with the issue id**
(see `KnownIssues.skipOn`), never deleted: remove the skip when the defect is fixed.

## KNOWN-1 — iOS: on-screen keyboard cannot be closed and covers checkout buttons

| | |
|---|---|
| Platform | iOS (My Demo App 2.3.0, iPhone 16 Pro simulator, iOS 27.0) |
| Severity | High: blocks the purchase flow |
| Blocked test | `CheckoutTest.userCompletesPurchase` (skipped on iOS; passes on Android) |

**Steps**
1. Add any product to the cart and tap **Proceed To Checkout**.
2. Tap the **Username** field on the Login screen and type any text.

**Expected:** the keyboard can be closed (Return key, a "Done" button or a tap outside the field),
and the **Login** button is reachable.

**Actual:** there is no way to close the keyboard; the **Login** button (and later **To Payment**,
**Review Order**) is fixed to the bottom of the screen and stays under the keyboard.
Appium's `hideKeyboard` fails with
`Did not know how to dismiss the keyboard. Try to dismiss it in the way supported by your application under test.`

**Suggested fix for the app:** close the keyboard on Return (`textFieldShouldReturn`) or on a tap outside
the fields, and/or keep bottom buttons above the keyboard (keyboard layout guide).
