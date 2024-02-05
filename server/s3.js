const s3 = require("@aws-sdk/client-s3")

const client = new s3.S3Client({
    endpoint: process.env.S3_ENDPOINT,
    region: process.env.S3_REGION,
    credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
    }
})
const BUCKET = "mad-storage"



class S3Interactor {

    constructor(bucketName = BUCKET) {
        const head = new s3.HeadBucketCommand({
            Bucket: bucketName
        })
        client.send(head).then((res) => {
            console.log("Bucket exists!", bucketName)
        }).catch(err => {
            console.log("bucket does not exist!", err)
            throw err
        })

    }

    async get(filename ) {
        const command = new s3.GetObjectCommand({
            Bucket: BUCKET,
            Key: filename
        })
        console.log("Get request from S3", command)
        try {
            const response = await client.send(command)
            return await response.Body.transformToByteArray()
        } catch (err) {
            console.error(err)
            return null
        }
    }

    async getStream(filename )  {
        const command = new s3.GetObjectCommand({
            Bucket: BUCKET,
            Key: filename
        })
        console.log("Get request from S3", command)
        try {
            const response = await client.send(command)
            return await response.Body.transformToWebStream()
        } catch (err) {
            console.error(err)
            return null
        }
    }

    async put(filename , contents ) {
        const command = new s3.PutObjectCommand({
            Bucket: BUCKET,
            Key: filename,
            Body: contents
        })
        console.log("Put request into S3", command)
        try {
            const response = await client.send(command)
            console.log(response)
            return {err: false}
        } catch(err) {
            console.error(err)
            return {err}
        }
    }


}

module.exports = {S3Interactor}