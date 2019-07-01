import object_detection.obj as model_1 
from flask import Flask 
from flask_socketio import SocketIO
import base64
import json

#initiate models
model_1.init()

app = Flask(__name__)
socketio = SocketIO(app)

#clients = []

@socketio.on('connect')
def handle_connect():
    print('Client connected')
    #clients.append(request.sid)

@socketio.on('disconnect')
def handle_disconnect():
    print('Client disconnected')
    #clients.remove(request.sid)

#recieve frames
@socketio.on('json')
def handle_frame(data):
    #decode image
    frame = base64.decodestring(json.dumps(data)['imageData'])

    #process frames
    data = model_1.process(frame)
    frame = model_1.draw(frame,data)

    #encode image
    data['imageData'] = base64.b64encode(frame)

     #send frames to client
    send(data, json=True)

    
'''@socketio.on('image_recv')
def handle_frame(frame_bytes):
    #convert array of bytes to image
    with open('test.jpg', 'wb') as frame:
        img.write(frame_bytes)
        
    #process frames
    data = model_1.process(frame)
    frame = model_1.draw(frame,data)

    #send frames to client
    with open('test.jpg', 'rb') as img:
        image_data = img.read()

    emit('image_send', {'image_data': image_data})'''


if __name__ == '__main__':
    socketio.run(app,host='localhost',port=12345)

