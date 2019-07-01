import obj as model_1 
from flask import Flask 
from flask_socketio import SocketIO

#initiate models
model_1.init()

app = Flask(__name__)
socketio = SocketIO(app)

@socketio.on('image_recv')
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

    emit('image_send', {'image_data': image_data})


if __name__ == '__main__':
    socketio.run(app)

