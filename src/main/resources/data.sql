INSERT INTO `users` (username) VALUES ('user');
INSERT INTO `cryptocurrencies` (symbol, name) VALUES ('USDT', 'Tether'), ('BTC', 'Bitcoin'), ('ETH', 'Ethereum');
INSERT INTO `cryptocurrencypairs` (symbol, first_currency_id, second_currency_id)
VALUES (
        'BTCUSDT',
        (SELECT id FROM `cryptocurrencies` WHERE symbol = 'BTC'),
        (SELECT id FROM `cryptocurrencies` WHERE symbol = 'USDT')
       ),(
    'ETHUSDT',
    (SELECT id FROM `cryptocurrencies` WHERE symbol = 'ETH'),
    (SELECT id FROM `cryptocurrencies` WHERE symbol = 'USDT'));
INSERT INTO `wallets` (user_id, currency_id, balance)
VALUES (
           (SELECT id FROM `users` WHERE username = 'user'),
           (SELECT id FROM `cryptocurrencies` WHERE symbol = 'USDT'),
           50000
       );