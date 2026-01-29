import 'package:flutter/material.dart';
import '../core/theme/app_theme.dart';
import '../models/player_model.dart';
import '../services/api_service.dart';
import 'login_screen.dart';
import 'player_profile_screen.dart';

/// Tela Home (Dashboard) - Acessível apenas após login
/// Mostra dados do jogador: Nickname, Nível, XP
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> with TickerProviderStateMixin {
  final ApiService _apiService = ApiService();

  Player? _player;
  bool _isLoading = true;
  String? _errorMessage;
  final TextEditingController _searchController = TextEditingController();

  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;

  // Animação do XP
  late AnimationController _xpAnimationController;
  late Animation<double> _xpScaleAnimation;
  late Animation<double> _xpOpacityAnimation;
  bool _showXpAnimation = false;
  bool _isGainingXp = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.elasticOut),
    );

    // Configura animação do XP
    _xpAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _xpScaleAnimation =
        TweenSequence<double>([
          TweenSequenceItem(tween: Tween(begin: 1.0, end: 1.5), weight: 50),
          TweenSequenceItem(tween: Tween(begin: 1.5, end: 1.0), weight: 50),
        ]).animate(
          CurvedAnimation(
            parent: _xpAnimationController,
            curve: Curves.easeInOut,
          ),
        );

    _xpOpacityAnimation = TweenSequence<double>([
      TweenSequenceItem(tween: Tween(begin: 0.0, end: 1.0), weight: 20),
      TweenSequenceItem(tween: Tween(begin: 1.0, end: 1.0), weight: 60),
      TweenSequenceItem(tween: Tween(begin: 1.0, end: 0.0), weight: 20),
    ]).animate(_xpAnimationController);

    _xpAnimationController.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        setState(() => _showXpAnimation = false);
        _xpAnimationController.reset();
      }
    });

    _loadPlayerData();
  }

  @override
  void dispose() {
    _animationController.dispose();
    _xpAnimationController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadPlayerData() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final player = await _apiService.getCurrentPlayer();
      setState(() => _player = player);
      _animationController.forward();
    } catch (e) {
      setState(() => _errorMessage = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _handleGainXp() async {
    if (_player == null || _isGainingXp) return;

    setState(() => _isGainingXp = true);

    try {
      final updatedPlayer = await _apiService.addXp(_player!.nickname, 100);
      setState(() {
        _player = updatedPlayer;
        _showXpAnimation = true;
      });
      _xpAnimationController.forward();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Erro ao ganhar XP!', style: AppTheme.pixelSmall),
          backgroundColor: AppTheme.error,
        ),
      );
    } finally {
      setState(() => _isGainingXp = false);
    }
  }

  Future<void> _handleLogout() async {
    await _apiService.logout();
    if (mounted) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.primaryDark,
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    return AppBar(
      backgroundColor: AppTheme.primaryPurple,
      title: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Image.asset('assets/images/fgreeengraps.gif', width: 40, height: 40),
          Text('GRAPES', style: AppTheme.pixelHeading),
        ],
      ),
      centerTitle: true,
      actions: [
        IconButton(
          icon: Icon(Icons.refresh, color: AppTheme.neonGreen),
          onPressed: _loadPlayerData,
          tooltip: 'Atualizar',
        ),
        IconButton(
          icon: Icon(Icons.logout, color: AppTheme.error),
          onPressed: _handleLogout,
          tooltip: 'Sair',
        ),
      ],
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return _buildLoadingState();
    }

    if (_errorMessage != null) {
      return _buildErrorState();
    }

    if (_player == null) {
      return _buildEmptyState();
    }

    return _buildDashboard();
  }

  Widget _buildLoadingState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            width: 48,
            height: 48,
            child: CircularProgressIndicator(
              strokeWidth: 4,
              valueColor: AlwaysStoppedAnimation(AppTheme.neonGreen),
            ),
          ),
          const SizedBox(height: 24),
          Text(
            'CARREGANDO...',
            style: AppTheme.pixelBody.copyWith(color: AppTheme.neonGreen),
          ),
        ],
      ),
    );
  }

  Widget _buildErrorState() {
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
              Icon(Icons.error_outline, color: AppTheme.error, size: 48),
              const SizedBox(height: 16),
              Text(
                'ERRO',
                style: AppTheme.pixelHeading.copyWith(color: AppTheme.error),
              ),
              const SizedBox(height: 8),
              Text(
                _errorMessage!,
                style: AppTheme.pixelSmall.copyWith(
                  color: AppTheme.lightPurple,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _loadPlayerData,
                style: AppTheme.retroButtonPrimary,
                child: Text('TENTAR NOVAMENTE', style: AppTheme.pixelButton),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Text('Nenhum dado encontrado', style: AppTheme.pixelBody),
    );
  }

  Widget _buildDashboard() {
    return RefreshIndicator(
      onRefresh: _loadPlayerData,
      color: AppTheme.neonGreen,
      backgroundColor: AppTheme.primaryPurple,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Saudação
            _buildGreeting(),
            const SizedBox(height: 32),

            // Card de Stats
            ScaleTransition(scale: _scaleAnimation, child: _buildStatsCard()),
            const SizedBox(height: 24),

            // Card de XP Progress
            _buildXpProgressCard(),
            const SizedBox(height: 24),

            // Busca de jogadores
            _buildSearchSection(),
            const SizedBox(height: 24),

            // Área de Ações (futuro)
            _buildActionsArea(),
          ],
        ),
      ),
    );
  }

  Widget _buildGreeting() {
    final hour = DateTime.now().hour;
    String greeting;
    String emoji;

    if (hour < 12 && hour > 5) {
      greeting = 'BOM DIA';
      emoji = '☀️';
    } else if (hour < 18 && hour > 12) {
      greeting = 'BOA TARDE';
      emoji = '🌤️';
    } else {
      greeting = 'BOA NOITE';
      emoji = '🌙';
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text(emoji, style: TextStyle(fontSize: 28)),
            const SizedBox(width: 12),
            Text(
              greeting,
              style: AppTheme.pixelSmall.copyWith(color: AppTheme.lightPurple),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Text(
          'Olá, ${_player!.nickname}!',
          style: AppTheme.pixelHeading.copyWith(
            color: AppTheme.neonGreen,
            fontSize: 20,
          ),
        ),
      ],
    );
  }

  Widget _buildStatsCard() {
    return Container(
      decoration: AppTheme.retroBorder,
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          // Nível
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: AppTheme.greenGlowBorder,
            child: Column(
              children: [
                Text('NÍVEL', style: AppTheme.pixelSmall),
                const SizedBox(height: 4),
                Text(
                  '${_player!.level}',
                  style: AppTheme.pixelTitle.copyWith(
                    color: AppTheme.neonGreen,
                    fontSize: 48,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Stats Row
          Row(
            children: [
              Expanded(
                child: _buildStatItem('XP', '${_player!.xp}', Icons.star),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildStatItem('EMAIL', _player!.email, Icons.email),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatItem(String label, String value, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.primaryDark,
        border: Border.all(color: AppTheme.accentPurple, width: 2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: AppTheme.accentPurple, size: 16),
              const SizedBox(width: 8),
              Text(label, style: AppTheme.pixelSmall),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }

  Widget _buildXpProgressCard() {
    final progress = _player!.levelProgress.clamp(0.0, 1.0);
    final xpToNext = _player!.xpToNextLevel;

    return Container(
      decoration: AppTheme.retroBorder,
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '> PROGRESSO',
            style: AppTheme.pixelBody.copyWith(color: AppTheme.neonGreen),
          ),
          const SizedBox(height: 16),

          // Barra de progresso
          Container(
            height: 24,
            decoration: BoxDecoration(
              color: AppTheme.primaryDark,
              border: Border.all(color: AppTheme.accentPurple, width: 2),
            ),
            child: Stack(
              children: [
                FractionallySizedBox(
                  widthFactor: progress,
                  child: Container(
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        colors: [AppTheme.accentPurple, AppTheme.neonGreen],
                      ),
                    ),
                  ),
                ),
                Center(
                  child: Text(
                    '${(progress * 100).toInt()}%',
                    style: AppTheme.pixelSmall.copyWith(color: AppTheme.white),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 12),

          Text(
            'Faltam $xpToNext XP para o nível ${_player!.level + 1}',
            style: AppTheme.pixelSmall,
          ),
          const SizedBox(height: 20),

          // Botão de ganhar XP com animação
          Stack(
            alignment: Alignment.center,
            children: [
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: _isGainingXp ? null : _handleGainXp,
                  style: AppTheme.retroButtonPrimary,
                  child: _isGainingXp
                      ? SizedBox(
                          width: 24,
                          height: 24,
                          child: CircularProgressIndicator(
                            strokeWidth: 3,
                            valueColor: AlwaysStoppedAnimation(
                              AppTheme.primaryDark,
                            ),
                          ),
                        )
                      : Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Image.asset(
                              'assets/images/heart_xp.png',
                              width: 24,
                              height: 24,
                            ),
                            const SizedBox(width: 12),
                            Text(
                              'GANHAR 100 XP',
                              style: AppTheme.pixelButton.copyWith(
                                color: AppTheme.primaryDark,
                              ),
                            ),
                          ],
                        ),
                ),
              ),

              // Animação flutuante do coração
              if (_showXpAnimation)
                AnimatedBuilder(
                  animation: _xpAnimationController,
                  builder: (context, child) {
                    return Transform.translate(
                      offset: Offset(0, -50 * _xpAnimationController.value),
                      child: Opacity(
                        opacity: _xpOpacityAnimation.value,
                        child: Transform.scale(
                          scale: _xpScaleAnimation.value,
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Image.asset(
                                'assets/images/heart_xp.png',
                                width: 32,
                                height: 32,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                '+100',
                                style: AppTheme.pixelBody.copyWith(
                                  color: AppTheme.neonGreen,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildActionsArea() {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(
          color: AppTheme.accentPurple.withValues(alpha: 0.3),
          width: 2,
          style: BorderStyle.solid,
        ),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Icon(
            Icons.construction,
            color: AppTheme.accentPurple.withValues(alpha: 0.5),
            size: 32,
          ),
          const SizedBox(height: 12),
          Text(
            'EM CONSTRUÇÃO',
            style: AppTheme.pixelSmall.copyWith(
              color: AppTheme.accentPurple.withValues(alpha: 0.5),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'Mais funcionalidades em breve!',
            style: AppTheme.pixelSmall.copyWith(
              color: AppTheme.lightPurple.withValues(alpha: 0.5),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchSection() {
    return Container(
      decoration: AppTheme.retroBorder,
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '> BUSCAR JOGADOR',
            style: AppTheme.pixelBody.copyWith(color: AppTheme.neonGreen),
          ),
          const SizedBox(height: 16),

          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _searchController,
                  style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
                  decoration: AppTheme.inputDecoration(
                    'NICKNAME',
                    icon: Icons.search,
                  ),
                  onSubmitted: (_) => _searchPlayer(),
                ),
              ),
              const SizedBox(width: 12),
              SizedBox(
                height: 52,
                child: ElevatedButton(
                  onPressed: _searchPlayer,
                  style: AppTheme.retroButtonGreen,
                  child: Icon(
                    Icons.arrow_forward,
                    color: AppTheme.primaryDark,
                    size: 24,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _searchPlayer() {
    final nickname = _searchController.text.trim();
    if (nickname.isEmpty) return;

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => PlayerProfileScreen(nickname: nickname),
      ),
    );
  }
}
