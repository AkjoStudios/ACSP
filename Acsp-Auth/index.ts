import express from "express";
import cors from "cors";
import supertokens from "supertokens-node";
import { middleware, errorHandler } from "supertokens-node/framework/express";

import { getWebsiteDomain, SuperTokensConfig } from "./config";


supertokens.init(SuperTokensConfig)
const app = express();

app.use(
    cors({
        origin: getWebsiteDomain(),
        allowedHeaders: ["content-type", ...supertokens.getAllCORSHeaders()],
        methods: ["GET", "PUT", "POST", "DELETE"],
        credentials: true,
    })
);

app.use(middleware());
app.use(errorHandler());

app.listen(3001, () => console.log(`ACSP Auth Server listening on port 3001...`));