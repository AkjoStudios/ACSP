import express from "express";
import cors from "cors";
import supertokens from "supertokens-node";
import { middleware, errorHandler } from "supertokens-node/framework/express";
import promBundle from "express-prom-bundle";
import basicAuth from "express-basic-auth";
import 'dotenv/config'

import { getWebsiteDomain, getBackendDomain, SuperTokensConfig } from "./config";

require('dotenv').config()

supertokens.init(SuperTokensConfig)
const app = express();

const metricsMiddleware = promBundle({
    includeMethod: true,
    includePath: true,
    includeStatusCode: true,
    includeUp: true,
    customLabels: {app_name: "Acsp-Auth"},
    promClient: {
        collectDefaultMetrics: {}
    },
});
app.use('/metrics', basicAuth({
    users: { 'agent_user': process.env.GRAFANA_AGENT_TOKEN ?? '' },
}));
app.use(metricsMiddleware);

app.use(
    cors({
        origin: [getWebsiteDomain(), getBackendDomain()],
        allowedHeaders: ["content-type", ...supertokens.getAllCORSHeaders()],
        methods: ["GET", "PUT", "POST", "DELETE"],
        credentials: true,
    })
);

app.use(middleware());
app.use(errorHandler());

app.listen(3001, () => console.log(`ACSP Auth Server listening on port 3001...`));