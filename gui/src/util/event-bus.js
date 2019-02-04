
const EventEmitter = require('events')

const eventEmitter = new EventEmitter()

const EventBus = {
    on: (event, callback) => eventEmitter.on(event, callback),
    send: (event, data) => eventEmitter.emit(event, data)
}

module.exports = EventBus