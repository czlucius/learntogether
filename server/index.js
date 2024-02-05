const express = require("express")
const dotenv = require("dotenv")
dotenv.config()
const firebaseAdmin = require("firebase-admin")
const OpenAI = require("openai")
const bodyParser = require("body-parser");

const app = express()
const serviceAccount = require("./service/learntogether-firebase.json")
const firebaseApp = firebaseAdmin.initializeApp({
    credential: firebaseAdmin.credential.cert(serviceAccount)
})
const auth = firebaseApp.auth()
const openai = new OpenAI({
    apiKey: process.env['OPENAI_API_KEY']
});

const {storageRouter} = require("./storage")

const SYSTEM_PROMPT = `
You are an assistant for homework questions supplied by the user.
You should give thorough explanations of the solution, going through it step by step.
You can give your answer formatted in Markdown.
You can give math expressions in LaTEX.
The audience is school-going students from primary school all the way to college.
`

app.post("/gpt", bodyParser.json(), async (req, res) => {
    let {request, history} = req.body
    const firebaseToken = req.headers.authorization
    console.log(firebaseToken)
    // for debugging purposes
    if (firebaseToken !== process.env.OPENAI_API_KEY) {


        let decoded
        try {
            decoded = await auth.verifyIdToken(firebaseToken)
        } catch (e) {
            decoded = false
        }
        if (!decoded) {
            return res.status(403).json({
                error: "Unauthorized!"
            })
        }
    }
    // const uid = decoded.uid
    history ??= []
    // TODO streaming response
    const response = await openai.chat.completions.create({
        model: "gpt-3.5-turbo",
        messages: [{
            role: 'system',
            content: SYSTEM_PROMPT
        }, ...history,
            {
            role: "user",
            content: request
        }],
        stream: false
    }
    )

    const choices = response.choices

    console.log(choices)
    let message = ''
    let update = history
    for (const choice of choices) {
        if (choice.message.role === "assistant") {
            message += choice.message.content
        }
        message += "\n"
        update.push(choice.message)
    }
    const send ={
        response: message,
        history: update
    }

    res.json(send)

})


async function send(tokens, message) {
    const multicast = {
        tokens, data: {
            ...message
        }, android: {
            priority: "high"
        }
    }
    if (message.notificationTitle) {
        // not undefined, so we set the notification.
        // if notificationTitle is undefined, it is determined that the user of this function does not want background notifications
        // foreground notifications will be handled differently.
        multicast.notification = {
            title: message.notificationTitle,
            body: message.info
        }
    }
    try {

        const response = await messaging.sendEachForMulticast(multicast)
        console.log('Successfully sent message to', uid, ", Response: ", response)

    } catch (e) {
        console.log('Error sending message:', e);
    }
}


app.post("/sendToFriendsFcm", async (req, res) => {
    const {mt, pt, name} = req.body
    // body is a list of profiles
    /*
            obj.put("username", username)
        obj.put("email", email)
        obj.put("hasface", isHasFace)
        obj.put("name", name)
        obj.put("profilepicurl", profilePicUrl)
        obj.put("uid", uid)
        obj.put("friends", JSONArray(friends))
        obj.put("phone", phone)
     */
    await send(pt.map(elem => elem.fcmtoken), {
        "action": "MEETUP_REQUEST",
        initiator_name: name,
        latitude: mt.latitude,
        longitude: mt.longitude,
        meetup_id: mt.id
    })
})

app.use("/storage", storageRouter)





const PORT = 5678
app.listen(PORT, () => {
    console.log(`Listening on ${PORT}`)
})
