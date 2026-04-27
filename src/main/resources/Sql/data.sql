INSERT INTO collectivities (id, number, name, locality, specialization, created_at) VALUES
('col-1', 1, 'Mpanorina',       'Ambatondrazaka', 'Riziculture', '2024-01-01'),
('col-2', 2, 'Dobo voalohany',  'Ambatondrazaka', 'Pisciculture', '2024-01-01'),
('col-3', 3, 'Tantely mamy',    'Brickaville',    'Apiculture',  '2024-01-01');

INSERT INTO members (id, last_name, first_name, birth_date, gender, address, profession, phone, email) VALUES
('C1-M1',  'Nom membre 1',  'Prénom membre 1',  '1980-02-01', 'M', 'Lot II V M Ambato.',  'Riziculteur', '0341234567', 'member.1@fed-agri.mg'),
('C1-M2',  'Nom membre 2',  'Prénom membre 2',  '1982-03-05', 'M', 'Lot II F Ambato.',    'Agriculteur', '0321234567', 'member.2@fed-agri.mg'),
('C1-M3',  'Nom membre 3',  'Prénom membre 3',  '1992-03-10', 'M', 'Lot II J Ambato.',    'Collecteur',  '0331234567', 'member.3@fed-agri.mg'),
('C1-M4',  'Nom membre 4',  'Prénom membre 4',  '1988-05-22', 'F', 'Lot A K 50 Ambato.',  'Distributeur','0381234567', 'member.4@fed-agri.mg'),
('C1-M5',  'Nom membre 5',  'Prénom membre 5',  '1999-08-21', 'M', 'Lot UV 80 Ambato.',   'Riziculteur', '0373434567', 'member.5@fed-agri.mg'),
('C1-M6',  'Nom membre 6',  'Prénom membre 6',  '1998-08-22', 'F', 'Lot UV 6 Ambato.',    'Riziculteur', '0372234567', 'member.6@fed-agri.mg'),
('C1-M7',  'Nom membre 7',  'Prénom membre 7',  '1998-01-31', 'M', 'Lot UV 7 Ambato.',    'Riziculteur', '0374234567', 'member.7@fed-agri.mg'),
('C1-M8',  'Nom membre 8',  'Prénom membre 8',  '1975-08-20', 'M', 'Lot UV 8 Ambato.',    'Riziculteur', '0370234567', 'member.8@fed-agri.mg'),
('C3-M1',  'Nom membre 9',  'Prénom membre 9',  '1988-01-02', 'M', 'Lot 33 J Antsirabe',  'Apiculteur',  '034034567',  'member.9@fed-agri.mg'),
('C3-M2',  'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'M', 'Lot 2 J Antsirabe',   'Agriculteur', '0338634567', 'member.10@fed-agri.mg'),
('C3-M3',  'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'M', 'Lot 8 KM Antsirabe',  'Collecteur',  '0338234567', 'member.11@fed-agri.mg'),
('C3-M4',  'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'F', 'Lot A K 50 Antsirabe','Distributeur','0382334567', 'member.12@fed-agri.mg'),
('C3-M5',  'Nom membre 13', 'Prénom membre 13', '1999-08-11', 'M', 'Lot UV 80 Antsirabe', 'Apiculteur',  '0373365567', 'member.13@fed-agri.mg'),
('C3-M6',  'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'F', 'Lot UV 6 Antsirabe',  'Apiculteur',  '0378234567', 'member.14@fed-agri.mg'),
('C3-M7',  'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'M', 'Lot UV 7 Antsirabe',  'Apiculteur',  '0374914567', 'member.15@fed-agri.mg'),
('C3-M8',  'Nom membre 16', 'Prénom membre 16', '1975-08-02', 'M', 'Lot UV 8 Antsirabe',  'Apiculteur',  '0370634567', 'member.16@fed-agri.mg');


INSERT INTO collectivity_members (id, collectivity_id, member_id, role, adhesion_date, active) VALUES
('CM-C1-M1', 'col-1', 'C1-M1', 'PRESIDENT',        '2025-01-01', TRUE),
('CM-C1-M2', 'col-1', 'C1-M2', 'VICE_PRESIDENT',    '2025-01-01', TRUE),
('CM-C1-M3', 'col-1', 'C1-M3', 'SECRETARY',         '2025-01-01', TRUE),
('CM-C1-M4', 'col-1', 'C1-M4', 'TREASURER',         '2025-01-01', TRUE),
('CM-C1-M5', 'col-1', 'C1-M5', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C1-M6', 'col-1', 'C1-M6', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C1-M7', 'col-1', 'C1-M7', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C1-M8', 'col-1', 'C1-M8', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE);


INSERT INTO collectivity_members (id, collectivity_id, member_id, role, adhesion_date, active) VALUES
('CM-C2-M1', 'col-2', 'C1-M1', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C2-M2', 'col-2', 'C1-M2', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C2-M3', 'col-2', 'C1-M3', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C2-M4', 'col-2', 'C1-M4', 'CONFIRMED_MEMBER',  '2025-01-01', TRUE),
('CM-C2-M5', 'col-2', 'C1-M5', 'PRESIDENT',         '2025-01-01', TRUE),
('CM-C2-M6', 'col-2', 'C1-M6', 'VICE_PRESIDENT',    '2025-01-01', TRUE),
('CM-C2-M7', 'col-2', 'C1-M7', 'SECRETARY',         '2025-01-01', TRUE),
('CM-C2-M8', 'col-2', 'C1-M8', 'TREASURER',         '2025-01-01', TRUE);

INSERT INTO collectivity_members (id, collectivity_id, member_id, role, adhesion_date, active) VALUES
('CM-C3-M1', 'col-3', 'C3-M1', 'PRESIDENT',        '2025-01-01', TRUE),
('CM-C3-M2', 'col-3', 'C3-M2', 'VICE_PRESIDENT',   '2025-01-01', TRUE),
('CM-C3-M3', 'col-3', 'C3-M3', 'SECRETARY',        '2025-01-01', TRUE),
('CM-C3-M4', 'col-3', 'C3-M4', 'TREASURER',        '2025-01-01', TRUE),
('CM-C3-M5', 'col-3', 'C3-M5', 'CONFIRMED_MEMBER', '2025-01-01', TRUE),
('CM-C3-M6', 'col-3', 'C3-M6', 'CONFIRMED_MEMBER', '2025-01-01', TRUE),
('CM-C3-M7', 'col-3', 'C3-M7', 'CONFIRMED_MEMBER', '2025-01-01', TRUE),
('CM-C3-M8', 'col-3', 'C3-M8', 'CONFIRMED_MEMBER', '2025-01-01', TRUE);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M3-S1', 'CM-C1-M3', 'C1-M1', NULL),
('SP-C1-M3-S2', 'CM-C1-M3', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M4-S1', 'CM-C1-M4', 'C1-M1', NULL),
('SP-C1-M4-S2', 'CM-C1-M4', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M5-S1', 'CM-C1-M5', 'C1-M1', NULL),
('SP-C1-M5-S2', 'CM-C1-M5', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M6-S1', 'CM-C1-M6', 'C1-M1', NULL),
('SP-C1-M6-S2', 'CM-C1-M6', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M7-S1', 'CM-C1-M7', 'C1-M1', NULL),
('SP-C1-M7-S2', 'CM-C1-M7', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C1-M8-S1', 'CM-C1-M8', 'C1-M6', NULL),
('SP-C1-M8-S2', 'CM-C1-M8', 'C1-M7', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M3-S1', 'CM-C2-M3', 'C1-M1', NULL),
('SP-C2-M3-S2', 'CM-C2-M3', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M4-S1', 'CM-C2-M4', 'C1-M1', NULL),
('SP-C2-M4-S2', 'CM-C2-M4', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M5-S1', 'CM-C2-M5', 'C1-M1', NULL),
('SP-C2-M5-S2', 'CM-C2-M5', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M6-S1', 'CM-C2-M6', 'C1-M1', NULL),
('SP-C2-M6-S2', 'CM-C2-M6', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M7-S1', 'CM-C2-M7', 'C1-M1', NULL),
('SP-C2-M7-S2', 'CM-C2-M7', 'C1-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C2-M8-S1', 'CM-C2-M8', 'C1-M6', NULL),
('SP-C2-M8-S2', 'CM-C2-M8', 'C1-M7', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C3-M3-S1', 'CM-C3-M3', 'C3-M1', NULL),
('SP-C3-M3-S2', 'CM-C3-M3', 'C3-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C3-M4-S1', 'CM-C3-M4', 'C3-M1', NULL),
('SP-C3-M4-S2', 'CM-C3-M4', 'C3-M2', NULL);

INSERT INTO member_sponsors (id, collectivity_member_id, sponsor_member_id, relationship_nature) VALUES
('SP-C3-M5-S1', 'CM-C3-M5', 'C3-M1', NULL),
('SP-C3-M5-S2', 'CM-C3-M5', 'C3-M2', NULL),
('SP-C3-M6-S1', 'CM-C3-M6', 'C3-M1', NULL),
('SP-C3-M6-S2', 'CM-C3-M6', 'C3-M2', NULL),
('SP-C3-M7-S1', 'CM-C3-M7', 'C3-M1', NULL),
('SP-C3-M7-S2', 'CM-C3-M7', 'C3-M2', NULL),
('SP-C3-M8-S1', 'CM-C3-M8', 'C3-M1', NULL),
('SP-C3-M8-S2', 'CM-C3-M8', 'C3-M2', NULL);

INSERT INTO membership_fees (id, collectivity_id, label, status, frequency, eligible_from, amount) VALUES
('cot-1', 'col-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),
('cot-2', 'col-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),
('cot-3', 'col-3', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01',  50000);

INSERT INTO financial_accounts (id, collectivity_id, account_type, initial_balance) VALUES
('C1-A-CASH', 'col-1', 'CASH', 0);

INSERT INTO financial_accounts (id, collectivity_id, account_type, holder_name, phone_number, initial_balance) VALUES
('C1-A-MOBILE-1', 'col-1', 'ORANGE_MONEY', 'Mpanorina', '0370489612', 0);

INSERT INTO financial_accounts (id, collectivity_id, account_type, initial_balance) VALUES
('C2-A-CASH', 'col-2', 'CASH', 0);

INSERT INTO financial_accounts (id, collectivity_id, account_type, holder_name, phone_number, initial_balance) VALUES
('C2-A-MOBILE-1', 'col-2', 'ORANGE_MONEY', 'Dobo voalohany', '0320489612', 0);

INSERT INTO financial_accounts (id, collectivity_id, account_type, initial_balance) VALUES
('C3-A-CASH', 'col-3', 'CASH', 0);


INSERT INTO payments (id, collectivity_id, member_id, amount, financial_account_id, payment_method, payment_date) VALUES
('PAY-C1-M1', 'col-1', 'C1-M1', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M2', 'col-1', 'C1-M2', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M3', 'col-1', 'C1-M3', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M4', 'col-1', 'C1-M4', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M5', 'col-1', 'C1-M5', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M6', 'col-1', 'C1-M6', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M7', 'col-1', 'C1-M7',  60000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('PAY-C1-M8', 'col-1', 'C1-M8',  90000, 'C1-A-CASH', 'CASH', '2026-01-01');

INSERT INTO transactions (id, collectivity_id, debited_member_id, amount, financial_account_id, payment_method, transaction_date) VALUES
('TRX-C1-M1', 'col-1', 'C1-M1', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M2', 'col-1', 'C1-M2', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M3', 'col-1', 'C1-M3', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M4', 'col-1', 'C1-M4', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M5', 'col-1', 'C1-M5', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M6', 'col-1', 'C1-M6', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M7', 'col-1', 'C1-M7',  60000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('TRX-C1-M8', 'col-1', 'C1-M8',  90000, 'C1-A-CASH', 'CASH', '2026-01-01');

INSERT INTO payments (id, collectivity_id, member_id, amount, financial_account_id, payment_method, payment_date) VALUES
('PAY-C2-M1', 'col-2', 'C1-M1',  60000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M2', 'col-2', 'C1-M2',  90000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M3', 'col-2', 'C1-M3', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M4', 'col-2', 'C1-M4', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M5', 'col-2', 'C1-M5', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M6', 'col-2', 'C1-M6', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('PAY-C2-M7', 'col-2', 'C1-M7',  40000, 'C2-A-MOBILE-1', 'MOBILE_MONEY', '2026-01-01'),
('PAY-C2-M8', 'col-2', 'C1-M8',  60000, 'C2-A-MOBILE-1', 'MOBILE_MONEY', '2026-01-01');

INSERT INTO transactions (id, collectivity_id, debited_member_id, amount, financial_account_id, payment_method, transaction_date) VALUES
('TRX-C2-M1', 'col-2', 'C1-M1',  60000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M2', 'col-2', 'C1-M2',  90000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M3', 'col-2', 'C1-M3', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M4', 'col-2', 'C1-M4', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M5', 'col-2', 'C1-M5', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M6', 'col-2', 'C1-M6', 100000, 'C2-A-CASH',     'CASH',         '2026-01-01'),
('TRX-C2-M7', 'col-2', 'C1-M7',  40000, 'C2-A-MOBILE-1', 'MOBILE_MONEY', '2026-01-01'),
('TRX-C2-M8', 'col-2', 'C1-M8',  60000, 'C2-A-MOBILE-1', 'MOBILE_MONEY', '2026-01-01');
