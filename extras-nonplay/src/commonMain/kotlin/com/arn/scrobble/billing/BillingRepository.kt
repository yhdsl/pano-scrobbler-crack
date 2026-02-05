package com.arn.scrobble.billing

import com.arn.scrobble.api.license.LicenseChecker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.days

private const val PUBLIC_KEY_BASE64 =
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnElQD+PNdex6IZ1nq58KDJPz40GBgOIbUs3GrbaPsONcEy8+AEhZmpPDcVB/e931pExsGPdRrjd2cplJ8pUXvxBG5knyJv7EPO3VUnppbipqYhaSe9bH4nK5kuNROB/J3mggVMxZmgoDe2QHacrNbnfjS96pFc58MAjQPPCn6TAXA1H3WajvNcRnplBYK7N0ap/YT1dbMato4fl/0iT1J57bDz+J+w/DcewOOg7YPWxVN+p9WZyLKwgQ8y/1QybEi9IYfIw3INqVS11vx5f+79ZkY+xGAM9JHm7T71dDZc4rJPibUnnQ+R5J2jFz564wdio6i1zpKwUpNQgYbfpkPQIDAQAB"

class BillingRepository(
    context: Any?,
    clientData: BillingClientData,
    openInBrowser: (url: String) -> Unit,
) : BaseBillingRepository(
    context,
    clientData,
    openInBrowser,
) {

    override val _proProductDetails by lazy { MutableStateFlow<MyProductDetails?>(null) }
    override val proProductDetails by lazy { _proProductDetails.asStateFlow() }
    override val purchaseMethods = listOf(
        PurchaseMethod(
            displayName = "Ko-fi",
            displayDesc = "Uses Paypal",
            link = "https://ko-fi.com/kawaiiDango"
        ),
        PurchaseMethod(
            displayName = "BuyMeACoffee",
            displayDesc = "Uses Stripe",
            link = "https://buymeacoffee.com/kawaiidango"
        )
    )

    override val needsActivationCode = true
    private val CHECK_EVERY_DAYS = 7
    private val LICENSE_CHECKING_SERVER = "https://license-sever.kawaiidango.workers.dev"

    override fun initBillingClient() {
        val productDetails =
            MyProductDetails(
                clientData.proProductId,
                "${clientData.appName} Supporter",
                "${clientData.appName} Supporter",
                "Become a ${clientData.appName} supporter",
                "$5 or more",
            )

        _proProductDetails.value = productDetails
    }

    override fun startDataSourceConnections() {
    }

    override fun endDataSourceConnections() {
    }

    override suspend fun queryPurchasesAsync() {
        val (receipt, _) = clientData.receipt.first()
        receipt ?: return
        checkAndStoreLicense(receipt)
    }

    override suspend fun checkAndStoreLicense(receipt: String) {
    }

    override fun verifyPurchase(data: String, signature: String?) = true

    override fun launchBillingFlow(purchaseMethod: PurchaseMethod, activity: Any?) {
        purchaseMethod.link?.let { openInBrowser(it) }
    }

}
