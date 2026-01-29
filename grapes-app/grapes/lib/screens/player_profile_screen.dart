import 'package:flutter/material.dart';
import '../core/theme/app_theme.dart';
import '../models/player_model.dart';
import '../services/api_service.dart';

/// Tela de perfil de outro jogador
class PlayerProfileScreen extends StatefulWidget {
  final String nickname;

  const PlayerProfileScreen({super.key, required this.nickname});

  @override
  State<PlayerProfileScreen> createState() => _PlayerProfileScreenState();
}

class _PlayerProfileScreenState extends State<PlayerProfileScreen> {
  final ApiService _apiService = ApiService();

  Player? _player;
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadPlayer();
  }

  Future<void> _loadPlayer() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final player = await _apiService.getPlayerByNickname(widget.nickname);
      setState(() => _player = player);
    } catch (e) {
      setState(() => _errorMessage = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppTheme.neonGreen),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          '< VOLTAR',
          style: AppTheme.pixelSmall.copyWith(color: AppTheme.neonGreen),
        ),
        titleSpacing: 0,
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/background.png'),
            fit: BoxFit.cover,
            colorFilter: ColorFilter.mode(Color(0x99000000), BlendMode.darken),
          ),
        ),
        child: SafeArea(child: _buildBody()),
      ),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation(AppTheme.neonGreen),
            ),
            const SizedBox(height: 16),
            Text('Buscando jogador...', style: AppTheme.pixelSmall),
          ],
        ),
      );
    }

    if (_errorMessage != null) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: AppTheme.error.withValues(alpha: 0.1),
              border: Border.all(color: AppTheme.error, width: 3),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.person_off, color: AppTheme.error, size: 48),
                const SizedBox(height: 16),
                Text(
                  'JOGADOR NÃO ENCONTRADO',
                  style: AppTheme.pixelBody.copyWith(color: AppTheme.error),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                Text(
                  '"${widget.nickname}"',
                  style: AppTheme.pixelSmall.copyWith(
                    color: AppTheme.lightPurple,
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    }

    return _buildProfile();
  }

  Widget _buildProfile() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          // Avatar/Ícone
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: AppTheme.primaryPurple.withValues(alpha: 0.8),
              border: Border.all(color: AppTheme.accentPurple, width: 4),
              boxShadow: [
                BoxShadow(
                  color: AppTheme.accentPurple.withValues(alpha: 0.4),
                  blurRadius: 20,
                ),
              ],
            ),
            child: Image.asset(
              'assets/images/grape_icon.png',
              width: 80,
              height: 80,
            ),
          ),
          const SizedBox(height: 24),

          // Nickname
          Text(
            _player!.nickname.toUpperCase(),
            style: AppTheme.pixelHeading.copyWith(
              color: AppTheme.neonGreen,
              fontSize: 20,
            ),
          ),
          const SizedBox(height: 32),

          // Card de Stats
          Container(
            width: double.infinity,
            decoration: AppTheme.retroBorder,
            padding: const EdgeInsets.all(24),
            child: Column(
              children: [
                Text(
                  '> STATS',
                  style: AppTheme.pixelBody.copyWith(color: AppTheme.neonGreen),
                ),
                const SizedBox(height: 24),

                // Nível
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 32,
                    vertical: 16,
                  ),
                  decoration: AppTheme.greenGlowBorder,
                  child: Column(
                    children: [
                      Text('NÍVEL', style: AppTheme.pixelSmall),
                      const SizedBox(height: 4),
                      Text(
                        '${_player!.level}',
                        style: AppTheme.pixelTitle.copyWith(
                          color: AppTheme.neonGreen,
                          fontSize: 40,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),

                // XP
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.star, color: AppTheme.accentPurple, size: 24),
                    const SizedBox(width: 8),
                    Text(
                      '${_player!.xp} XP',
                      style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
