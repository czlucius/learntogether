const bodyParser = require("body-parser")
const {S3Interactor} = require("./s3")
const multer = require("multer")
const upload = multer({
    limits: {fileSize: 4 * (10 ** 6)}
})
const raw = bodyParser.raw({
    limit: "1mb", type: ["*/*"]
})
const express = require("express")
const storageRouter = express.Router()
const s3Interactor = new S3Interactor()

storageRouter.put("/upload", raw, async (req, res) => {
    const binary = req.body
    let name = req.headers["X-File-Name"]
    name ||= crypto.randomUUID()
    const response = await s3Interactor.put(binary)
    if (response.err) {
        res.status(500).json({
            error: "Failed to upload"
        })
    } else {
        res.status(200).json({
            error: false,
            success: "Request succeeded.",
            filename: name,
            url: `https://mad-storage.s3.eu-central-003.backblazeb2.com/${name}`
        })
    }

})
import { pipeline } from "node:stream/promises";
/**
 * Warning: a huge security risk but no choice for CSAD.
 * In a real project PLEASE check for authentication.
 */
storageRouter.get("/serve/:filename", async (req, res) => {
    const filename = req.params.filename
    const contents = await s3Interactor.getStream(filename) // returns a bytearray.
    res.writeHead(200, {"Content-Type": 'application/octet-stream'})
    res.end(contents)
    // (await s3Interactor.getStream(filename)).pipe(res)
    // res.write()
    // contents.pipeTo(res)
})

module.exports = {storageRouter}