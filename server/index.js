const express = require("express")
const dotenv = require("dotenv")
dotenv.config()
const firebaseAdmin = require("firebase-admin")
const OpenAI = require("openai")
const bodyParser = require("body-parser");

const app = express()
const serviceAccount = require("./learntogether-firebase.json")
const firebaseApp = firebaseAdmin.initializeApp({
    credential: firebaseAdmin.credential.cert(serviceAccount)
})
const auth = firebaseApp.auth()
const openai = new OpenAI({
    apiKey: process.env['OPENAI_API_KEY']
});


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
    let update = []
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




const PORT = 5678
app.listen(PORT, () => {
    console.log(`Listening on ${PORT}`)
})