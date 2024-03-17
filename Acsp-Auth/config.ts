import {TypeInput} from "supertokens-node/types";
import Session from "supertokens-node/recipe/session";
import Dashboard from "supertokens-node/recipe/dashboard";
import UserRoles from "supertokens-node/recipe/userroles";

export function getApiDomain() {
    const apiPort = process.env.APP_API_PORT ?? 3001;
    return process.env.APP_API_URL ?? `http://localhost:${apiPort}`;
}

export function getWebsiteDomain() {
    const websitePort = process.env.APP_WEBSITE_PORT ?? 3000;
    return process.env.APP_WEBSITE_URL ?? `http://localhost:${websitePort}`;
}

export const SuperTokensConfig: TypeInput = {
    framework: "express",
    supertokens: {
        connectionURI: process.env.SUPERTOKENS_URI ?? "https://try.supertokens.io",
    },
    appInfo: {
        appName: "ACSP Auth",
        apiDomain: getApiDomain(),
        websiteDomain: getWebsiteDomain(),
    },
    recipeList: [
        Session.init(),
        Dashboard.init(),
        UserRoles.init(),
    ],
}